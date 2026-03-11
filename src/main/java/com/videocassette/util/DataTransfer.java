package com.videocassette.util;

import java.util.HashMap;
import java.util.Map;

/**
 * La classe DataTransfer est une boîte à outils qui permet de passer des informations
 * entre deux pages (par exemple, donner le nom d'un film sélectionné à la page d'édition).
 */
public class DataTransfer {
    // Un dictionnaire (Map) pour stocker les données temporairement
    private static final Map<String, Object> data = new HashMap<>();

    /**
     * Enregistre une information dans la boîte.
     */
    public static void put(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Récupère une information sans la supprimer.
     */
    public static Object get(String key) {
        return data.get(key);
    }

    /**
     * Récupère une information et la retire de la boîte (pour faire de la place).
     */
    public static Object remove(String key) {
        return data.remove(key);
    }

    /**
     * Vérifie si une information spécifique est présente.
     */
    public static boolean has(String key) {
        return data.containsKey(key);
    }
}
