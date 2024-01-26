package com.ht.elearning.utils;

import java.util.List;

public record QueryPair<T>(List<T> results, Long total) {
}
