package com.avrgaming.civcraft.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public final class DiskMaintenance {
    private DiskMaintenance() {}

    public static void pruneByAge(Path dir, int days) throws IOException {
        if (dir == null || days <= 0) return;
        if (!Files.exists(dir)) return;

        long cutoff = System.currentTimeMillis() - days * 24L * 3600L * 1000L;

        Stream<Path> s = Files.walk(dir);
        try {
            for (Path p : (Iterable<Path>) s::iterator) {
                if (!Files.isRegularFile(p)) continue;
                try {
                    long t = Files.getLastModifiedTime(p).toMillis();
                    if (t < cutoff) Files.deleteIfExists(p);
                } catch (Exception ignored) {}
            }
        } finally {
            s.close();
        }
    }

    public static void pruneByQuota(Path dir, long maxBytes) throws IOException {
        if (dir == null || maxBytes <= 0) return;
        if (!Files.exists(dir)) return;

        class F {
            final Path p; final long size; final long mtime;
            F(Path p, long size, long mtime) { this.p = p; this.size = size; this.mtime = mtime; }
        }

        List<F> files = new ArrayList<F>();
        long total = 0L;

        Stream<Path> s = Files.walk(dir);
        try {
            for (Path p : (Iterable<Path>) s::iterator) {
                if (!Files.isRegularFile(p)) continue;
                try {
                    long size = Files.size(p);
                    long mtime = Files.getLastModifiedTime(p).toMillis();
                    files.add(new F(p, size, mtime));
                    total += size;
                } catch (Exception ignored) {}
            }
        } finally {
            s.close();
        }

        if (total <= maxBytes) return;

        files.sort(new Comparator<F>() {
            @Override public int compare(F a, F b) {
                return (a.mtime < b.mtime) ? -1 : ((a.mtime == b.mtime) ? 0 : 1);
            }
        });

        for (F f : files) {
            try {
                Files.deleteIfExists(f.p);
                total -= f.size;
                if (total <= maxBytes) break;
            } catch (Exception ignored) {}
        }
    }
}
