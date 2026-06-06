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
        
        playlist.add(new Song("Daniel Caesar", "We Find Love", "C:/Users/Hasbi Juadi/Downloads/Daniel Caesar - We Find Love.mp3"));
        playlist.add(new Song("Raissa Anggiani", "Losing Us", "C:/Users/Hasbi Juadi/Downloads/Raissa Anggiani - Losing Us.mp3"));
        playlist.add(new Song("Sienna Spiro", "The Visitor", "C:/Users/Hasbi Juadi/Downloads/SIENNA SPIRO - The Visitor.mp3"));
        playlist.add(new Song("Rafi Sudirman", "Fell in Love (Again)", "C:/Users/Hasbi Juadi/Downloads/Rafi Sudirman - Fell in Love (Again).mp3"));

        observablePlaylist = FXCollections.observableList(playlist);
        songListView.setItems(observablePlaylist);

        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentSongIndex = playlist.indexOf(newValue);
                loadSong(currentSongIndex);
                if (isPlaying) {
                    playMedia();
                }
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

        songListView.refresh();
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
            File file = new File(currentSong.getFilePath());
            
            if (!file.exists()) {
                currentTrackLabel.setText("File tidak ditemukan: " + currentSong.getTitle());
                return;
            }

            String mediaUri = file.toURI().toString(); 
            Media media = new Media(mediaUri);
            mediaPlayer = new MediaPlayer(media);
            
            mediaPlayer.setVolume(volumeSlider.getValue() / 100);
            mediaPlayer.setOnEndOfMedia(this::handleNext);

            if (isPlaying) {
                mediaPlayer.play();
            }
        } catch (Exception e) {
            currentTrackLabel.setText("Error loading file: " + currentSong.getTitle());
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

        if (currentSongIndex < playlist.size() - 1) {
            currentSongIndex++;
        } else {
            currentSongIndex = 0; 
        }
        loadSong(currentSongIndex);
    }

    @FXML
    private void handlePrevious() {
        if (playlist.isEmpty()) return;

        if (currentSongIndex > 0) {
            currentSongIndex--;
        } else {
            currentSongIndex = playlist.size() - 1; 
        }
        loadSong(currentSongIndex);
    }

    @FXML
    private void handleAddNewSong() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File Musik MP3");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Audio Files (*.mp3)", "*.mp3")
        );

        Stage stage = (Stage) songListView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String fullPath = selectedFile.getAbsolutePath();
            String fileName = selectedFile.getName().replace(".mp3", "");
            
            fileName = fileName.replace(" - Local File", "");
            fileName = fileName.replace("- Local File", "");
            
            observablePlaylist.add(new Song("Local File", fileName, fullPath));
        }
    }

    @FXML
    private void handleRemoveSong() {
        Song selectedSong = songListView.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            int selectedIndex = playlist.indexOf(selectedSong);
            observablePlaylist.remove(selectedSong);
            
            if (playlist.isEmpty()) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    mediaPlayer = null;
                }
                currentTrackLabel.setText("Now Playing: None");
                isPlaying = false;
                playPauseButton.setText("Play");
            } else {
                if (currentSongIndex == selectedIndex) {
                    if (currentSongIndex >= playlist.size()) {
                        currentSongIndex = playlist.size() - 1;
                    }
                    loadSong(currentSongIndex);
                } else if (currentSongIndex > selectedIndex) {
                    currentSongIndex--;
                    songListView.getSelectionModel().select(currentSongIndex);
                }
            }
        }
    }
}