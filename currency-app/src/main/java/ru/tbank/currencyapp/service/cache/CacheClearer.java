package ru.tbank.currencyapp.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tbank.currencyapp.config.ApplicationConfig;

@Service
@Slf4j
@RequiredArgsConstructor
class CacheClearer {

    private final CacheManager cacheManager;
    private final ApplicationConfig applicationConfig;

    /**
     * Clears cache every hour
     */
    @Scheduled(cron = "0 0 */1 * * *")
    public void clearCache() {
        log.info("Clearing cache...");
        evictCurrencyCache();
    }

    private void evictCurrencyCache() {
        log.info("Clear cache inside evictCurrencyCache method");
        Cache cbCurrenciesCache = cacheManager.getCache(applicationConfig.centralBankCacheName());
        if (cbCurrenciesCache != null)
            cbCurrenciesCache.clear();
    }

}
