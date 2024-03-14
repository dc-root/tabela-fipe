package com.alura.averagevehicles.service;

import java.util.List;

public interface IConverterDados {
    <T> T obterDados(String json, Class<T> classe);

    <T> List<T> obterLista(String json, Class<T> classe);
}
