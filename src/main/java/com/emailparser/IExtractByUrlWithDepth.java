package com.emailparser;

import java.util.Set;

public interface IExtractByUrlWithDepth {
    Set<String> extract(String url, int depth);
}
