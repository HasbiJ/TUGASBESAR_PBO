module impjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media; // <-- Tambahkan baris ini agar MediaPlayer bisa jalan
    
    // PENTING: Mengizinkan JavaFX membaca file FXML dan Controller Music Player Anda
    opens com.praktikumjavafx.musicplayer to javafx.fxml;
    exports com.praktikumjavafx.musicplayer;

    // Package tugas/praktikum Anda yang lain
    exports com.praktikumjavafx.stageandscene;
    exports com.praktikumjavafx.layoutdandatabase;
    opens com.praktikumjavafx.layoutdandatabase to javafx.fxml;
}
