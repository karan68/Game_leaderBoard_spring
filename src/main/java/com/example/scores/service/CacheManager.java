package com.example.scores.service;

import com.example.scores.entity.Score;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheManager {
    private static final int MAX_CACHE_SIZE = 1000; // Maximum number of entries in the cache
    private static final int CACHE_EXPIRY_MINUTES = 30; // Cache expiry time in minutes

    private static final Cache<String, List<Score>> TOP_SCORES_CACHE = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(CACHE_EXPIRY_MINUTES, TimeUnit.MINUTES)
            .build();


    public static Cache<String, List<Score>> getTopScoresCache() {
        return TOP_SCORES_CACHE;
    }
}
