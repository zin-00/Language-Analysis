package main;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private Clip backgroundMusic;
    private Clip correctSound;
    private Clip incorrectSound;
    private Clip neutralSound;
    private Clip gameBackgroundMusic;

    public SoundManager() {
        try {
            // Load background music
            AudioInputStream backgroundStream = AudioSystem.getAudioInputStream(new File("res/sounds/backgroundV1.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(backgroundStream);
            
            // Load background music
            AudioInputStream gameBackgroundStream = AudioSystem.getAudioInputStream(new File("res/sounds/relaxing-piano.wav"));
            gameBackgroundMusic = AudioSystem.getClip();
            gameBackgroundMusic.open(gameBackgroundStream);

            // Load sound effects
            AudioInputStream correctStream = AudioSystem.getAudioInputStream(new File("res/sounds/correct.wav"));
            correctSound = AudioSystem.getClip();
            correctSound.open(correctStream);

            AudioInputStream incorrectStream = AudioSystem.getAudioInputStream(new File("res/sounds/incorrect.wav"));
            incorrectSound = AudioSystem.getClip();
            incorrectSound.open(incorrectStream);

            AudioInputStream neutralStream = AudioSystem.getAudioInputStream(new File("res/sounds/sound1.wav"));
            neutralSound = AudioSystem.getClip();
            neutralSound.open(neutralStream);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public void playGameBackgroundMusic() {
    	if(gameBackgroundMusic != null) {
    		gameBackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
    	}
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null || gameBackgroundMusic != null) {
            backgroundMusic.stop();
            gameBackgroundMusic.stop();
        }
    }

    public void playCorrectSound() {
        playSound(correctSound);
    }

    public void playIncorrectSound() {
        playSound(incorrectSound);
    }

    public void playNeutralSound() {
        playSound(neutralSound);
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
}

