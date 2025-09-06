package com.avrgaming.civcraft.database;

import com.avrgaming.civcraft.main.CivLog;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

public class ConnectionPool {

	private static HikariDataSource ds;
	private static final AtomicLong connectionsRequested = new AtomicLong(0);

	public static void init(String host, int port, String database, String username, String password,
							int maxPoolSize, int minIdle) {
		if (ds != null && !ds.isClosed()) return;

		HikariConfig cfg = new HikariConfig();

		// FORCE Connector/J 8, ook als er elders nog een 5.1-driver op de classpath staat
		cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

		String baseUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
		String params = "useUnicode=true" +
				"&characterEncoding=utf8" +
				"&serverTimezone=UTC" +
				"&sslMode=REQUIRED" +                 // TLS aan, zonder servercert-verificatie
				"&enabledTLSProtocols=TLSv1.2";       // Java 8

		cfg.setJdbcUrl(baseUrl + "?" + params);
		cfg.setUsername(username);
		cfg.setPassword(password);

		Properties dsProps = new Properties();
		dsProps.put("cachePrepStmts", "true");
		dsProps.put("prepStmtCacheSize", "250");
		dsProps.put("prepStmtCacheSqlLimit", "2048");
		dsProps.put("useServerPrepStmts", "true");
		// GEEN useSSL/verifyServerCertificate hier; sslMode regelt alles.
		cfg.setDataSourceProperties(dsProps);

		cfg.setMaximumPoolSize(maxPoolSize > 0 ? maxPoolSize : 10);
		cfg.setMinimumIdle(minIdle > 0 ? minIdle : 2);
		cfg.setPoolName("CivCraft");

		cfg.setConnectionTimeout(10_000L);
		cfg.setIdleTimeout(600_000L);
		cfg.setMaxLifetime(18_000_000L);

		ds = new HikariDataSource(cfg);
		CivLog.info("[SQL] HikariCP initialised for " + host + ":" + port + "/" + database);
	}

	public static Connection getConnection() throws SQLException {
		if (ds == null) throw new SQLException("HikariCP has not been initialised");
		connectionsRequested.incrementAndGet();
		return ds.getConnection();
	}

	public static void shutdown() {
		if (ds != null) {
			try {
				ds.close();
				CivLog.info("[SQL] HikariCP pool closed");
			} catch (Throwable t) {
				CivLog.error("[SQL] Error closing HikariCP pool");
				t.printStackTrace();
			} finally {
				ds = null;
			}
		}
	}

	public static final class Stats {
		private final int active;
		private final int idle;
		private final int total;
		private final int waiting;
		private final long requested;

		private Stats(int active, int idle, int total, int waiting, long requested) {
			this.active = active;
			this.idle = idle;
			this.total = total;
			this.waiting = waiting;
			this.requested = requested;
		}

		public long getConnectionsRequested() { return requested; }
		public int getTotalFree() { return idle; }
		public int getTotalLeased() { return active; } // alias voor 'leased' (actief) voor je oude debugcmd
		public int getActive() { return active; }
		public int getTotal() { return total; }
		public int getWaiting() { return waiting; }
	}

	public Stats getStats() {
		HikariPoolMXBean mx = (ds != null) ? ds.getHikariPoolMXBean() : null;
		int active  = (mx != null) ? mx.getActiveConnections() : 0;
		int idle    = (mx != null) ? mx.getIdleConnections() : 0;
		int total   = (mx != null) ? mx.getTotalConnections() : 0;
		int waiting = (mx != null) ? mx.getThreadsAwaitingConnection() : 0;
		long requested = connectionsRequested.get();
		return new Stats(active, idle, total, waiting, requested);
	}
}
