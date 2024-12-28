package net.technearts.lang.fun;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "math")
public interface Config {

    @WithName("precision")
    Integer precision();

}