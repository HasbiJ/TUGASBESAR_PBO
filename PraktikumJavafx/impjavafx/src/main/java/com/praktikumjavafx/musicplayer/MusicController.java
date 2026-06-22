package com.praktikumjavafx.musicplayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MusicController {

    @FXML private ListView<Song> songListView; 
    @FXML private Label currentTrackLabel;
    @FXML private Slider volumeSlider;
    @FXML private Button playPauseButton;

    private List<Song> playlist; 
    private ObservableList<Song> observablePlaylist; 
    private MediaPlayer mediaPlayer;
    private int currentSongIndex = 0;
    private boolean isPlaying = false;

    @FXML
    public void initialize() {
        playlist = new ArrayList<>();
        
        // Menggunakan path relatif dari folder resources
        String pathPrefix = "/com/praktikumjavafx/musicplayer/";

        // Menambahkan lagu dengan URL resource
        addSongToPlaylist("Daniel Caesar", "We Find Love", pathPrefix + "Daniel Caesar - We Find Love.mp3");
        addSongToPlaylist("Raissa Anggiani", "Losing Us", pathPrefix + "Raissa Anggiani - Losing Us.mp3");
        addSongToPlaylist("Sienna Spiro", "The Visitor", pathPrefix + "SIENNA SPIRO - The Visitor.mp3");
        addSongToPlaylist("Rafi Sudirman", "Fell in Love (Again)", pathPrefix + "Rafi Sudirman - Fell in Love (Again).mp3");

        observablePlaylist = FXCollections.observableList(playlist);
        songListView.setItems(observablePlaylist);

        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentSongIndex = playlist.indexOf(newValue);
                loadSong(currentSongIndex);
            }
        });

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue() / 100);
            }
        });

        if (!playlist.isEmpty()) {
            loadSong(currentSongIndex);
        }
    }

    private void addSongToPlaylist(String artist, String title, String resourcePath) {
        URL url = getClass().getResource(resourcePath);
        if (url != null) {
            playlist.add(new Song(artist, title, url.toExternalForm()));
        } else {
            System.err.println("File tidak ditemukan: " + resourcePath);
        }
    }

    private void loadSong(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); 
        }

        Song currentSong = playlist.get(index); 
        currentTrackLabel.setText("Now Playing: " + currentSong.getTitle());
        songListView.getSelectionModel().select(index);

        try {
            // Menggunakan URL string yang sudah di-generate
            Media media = new Media(currentSong.getFilePath());
            mediaPlayer = new MediaPlayer(media);
            
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            mediaPlayer.setOnEndOfMedia(this::handleNext);

            if (isPlaying) {
                mediaPlayer.play();
            }
        } catch (Exception e) {
            currentTrackLabel.setText("Error loading: " + currentSong.getTitle());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlayPause() {
        if (isPlaying) {
            pauseMedia();
        } else {
            playMedia();
        }
    }

    private void playMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            isPlaying = true;
            playPauseButton.setText("Pause");
        }
    }

    private void pauseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            playPauseButton.setText("Play");
        }
    }

    @FXML
    private void handleNext() {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        loadSong(currentSongIndex);
    }

    @FXML
    private void handlePrevious() {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        loadSong(currentSongIndex);
    }

    @FXML
    private void handleAddNewSong() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files (*.mp3)", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            observablePlaylist.add(new Song("Local File", selectedFile.getName(), selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handleRemoveSong() {
        Song selectedSong = songListView.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            observablePlaylist.remove(selectedSong);
            if (playlist.isEmpty() && mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
        }
    }
}