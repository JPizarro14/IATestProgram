package com.ia;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Parseador {

    public static void main(String[] args) {
        String relativePath = "src/main/resources/NC/Tema9.txt";
        String[] wordsToDeletePregunta = {"Pregunta ", "Correcta", "Incorrecta", "Se punt", "No marcadasMarcar", "Enunciado de la pregunta"};
        String[] wordsToDeleteRespuesta = {"a.", "b.", "c.", "d."};

        try {
            // Obtener la ruta al recurso
            Path resourcePath = Path.of(relativePath);

            // Crear un archivo temporal
            Path tempFile = Files.createTempFile("tempFile", ".txt");

            try (BufferedReader reader = Files.newBufferedReader(resourcePath);
                BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                boolean contienePregunta = false;
                boolean contieneRespuesta = false;
                String line;
                while ((line = reader.readLine()) != null) {
                    if (containsWordsPregunta(line, wordsToDeletePregunta)) {
                        contienePregunta = true;
                    }
                    if(containsWordsRespuesta(line, wordsToDeleteRespuesta)) {
                        contieneRespuesta = true;
                    }
                    if (contieneRespuesta && !contienePregunta && !containsWordsPregunta(line, wordsToDeletePregunta) && !containsWordsRespuesta(line, wordsToDeleteRespuesta)) {
                        writer.write("-" + line);
                        writer.newLine();
                        contieneRespuesta = false;
                    }
                    if (contienePregunta && !containsWordsPregunta(line, wordsToDeletePregunta) && !containsWordsRespuesta(line, wordsToDeleteRespuesta)) {
                        writer.write("|" + line);
                        writer.newLine();
                        contienePregunta = false;
                    }
                }
            }
            // Reemplazar el archivo original con el temporal
            Files.move(tempFile, resourcePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean containsWordsPregunta(String line, String[] words) {
        for (String word : words) {
            if (line.startsWith(word)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsWordsRespuesta(String line, String[] words) {
        // Verificar si la línea está vacía o solo contiene espacios en blanco
        if (line.trim().isEmpty()) {
            return true;
        }
        for (String word : words) {
            if (line.startsWith(word)) {
                return true;
            }
        }
        return false;
    }
}