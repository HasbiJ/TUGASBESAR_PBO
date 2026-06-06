package com.praktikumjavafx.musicplayer;

public class Song {
    private final String artis;
    private final String judul;
    private final String lokasiFile;

    public Song(String artis, String judul, String lokasiFile) {
        this.artis = artis;
        this.judul = judul;
        this.lokasiFile = lokasiFile;
    }

    public String getArtist() {
        return artis;
    }

    public String getTitle() {
        return judul;
    }

    public String getFilePath() {
        return lokasiFile;
    }

    @Override
    public String toString() {
        if (artis == null || artis.trim().isEmpty()) {
            return judul;
        }
        return artis + " - " + judul;
    }
}