package ua.softserve.academy.kv030.authservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by Miha on 21.12.2017.
 */
@Component
@ConfigurationProperties(prefix = "statistics")
public class StatisticProperties {

    private boolean enabled;

    private String exchange;

    private String key;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
