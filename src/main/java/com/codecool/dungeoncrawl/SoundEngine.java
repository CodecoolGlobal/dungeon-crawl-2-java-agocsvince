package com.codecool.dungeoncrawl;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.Objects;

public class SoundEngine {
    private boolean isMuted = false;
    private final Mixer mixer;

    private final Random rand = new Random();
    private final ArrayList<Clip> musicClips = new ArrayList<>();


    private final File musicFolder = new File("./music/");

    private float volume = -30;
    private int currentMusicIndex = 0;
    private Clip currentClip;
    private Clip ambientSound;

    private static final LinkedHashMap<String, String> nameToFileName = new LinkedHashMap<>();
    private final HashMap<String, ArrayList<Clip>> fileNameToClip = new HashMap<>();

    //name -> fileName -> Clips
    //Place SFX file loads here
    static {
        nameToFileName.put("Creepy Dungeon Ambience Background Audio Sound Effects", "ambient");
        nameToFileName.put("ES_Footsteps Cement 6 - SFX Producer", "footstep");
        nameToFileName.put("ES_Footsteps Cement 11 - SFX Producer", "footstep");
        nameToFileName.put("ES_Bone Break 7 - SFX Producer", "skeleton");
        nameToFileName.put("ES_Knife Slashing Rope - SFX Producer", "attack");
        nameToFileName.put("mixkit-dagger-woosh-1487", "attack");
        nameToFileName.put("mixkit-metal-hit-woosh-1485", "attack");
        nameToFileName.put("mixkit-metallic-sword-scrape-2799", "attack");
        nameToFileName.put("mixkit-hammer-hit-on-wood-830", "pickup");
        nameToFileName.put("mixkit-metal-scrape-809", "pickup");
        nameToFileName.put("Zapper Pickup - Sound effect for editing", "pickup");
        nameToFileName.put("impact_rock_large_rockpool_dry_001", "golem");
        nameToFileName.put("Blastwave_FX_CementWallHit_BW.17122", "golem");
        nameToFileName.put("glitchedtones_Door+Bathroom+Unlock+02", "doorOpen");
        nameToFileName.put("127879882", "zombie");
        nameToFileName.put("Pubg- Molotov pickup sound Harsh Yadav yt", "potionDrink");
        nameToFileName.put("Potion  1 (Sound Effect) Diablo II", "potionDrink");

    }

    public SoundEngine() {
        mixer = getDefaultMixer();
        //Read sound files
        try {
            readMusicFiles();
            readSoundFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        playAmbientSound();
        playMusic();
    }

    private void readSoundFiles() {
        nameToFileName.forEach((k, v) -> {
            try {
                File fileToRead = new File("./sounds/" + k + ".wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(fileToRead);
                DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
                Clip soundFX = (Clip) AudioSystem.getLine(dataInfo);
                soundFX.open(audioStream);
                if (fileNameToClip.containsKey(v)) {
                    fileNameToClip.get(v).add(soundFX);
                } else {
                    if (v.equals("ambient"))
                        ambientSound = soundFX;
                    ArrayList<Clip> tempList = new ArrayList<>();
                    tempList.add(soundFX);
                    fileNameToClip.put(v, tempList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Mixer getDefaultMixer() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixers) {
            if (info.getDescription().contains("default"))
                return AudioSystem.getMixer(info);
        }
        return null;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void play(String soundFXName) {
        ArrayList<Clip> soundFXList = fileNameToClip.get(soundFXName);
        Clip clipToPlay = soundFXList.get(rand.nextInt(soundFXList.size()));
        clipToPlay.setMicrosecondPosition(0);

        clipToPlay.start();
    }

    private void readMusicFiles() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        for (final File musicFile : Objects.requireNonNull(musicFolder.listFiles())) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
            Clip musicClip = (Clip) AudioSystem.getLine(dataInfo);
            musicClip.open(audioStream);
            musicClip.addLineListener(this::playNextTrack);
            musicClips.add(musicClip);
        }
        Collections.shuffle(musicClips);
    }

    public void decreaseVolume() {
        changeVolume(-10);
    }

    public void increaseVolume() {
        changeVolume(10);
    }

    public void changeVolume(float value) {
        volume += value;
        volume = Math.min(Math.max(-40, volume), 0);
        Line[] lines = mixer.getSourceLines();
        for (Line line : lines) {
            if (line == currentClip && line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl ctrl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                ctrl.setValue(volume);
            }
        }
    }

    public void toggleMute() {
        Line[] lines = mixer.getSourceLines();
        for (Line line : lines) {
            if (line.isControlSupported(BooleanControl.Type.MUTE)) {
                BooleanControl bc = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                if (!isMuted) {
                    if (bc != null) {
                        bc.setValue(true);
                    }
                } else {
                    if (bc != null) {
                        bc.setValue(false);
                    }
                }
            }
        }
        isMuted = !isMuted;
    }

    private void playAmbientSound() {
        FloatControl ambientVolume = (FloatControl) ambientSound.getControl(FloatControl.Type.MASTER_GAIN);
        ambientVolume.setValue(-15);
        ambientSound.addLineListener(e -> {
            if (e.getType() == LineEvent.Type.STOP) {
                ambientSound.setMicrosecondPosition(0);
                ambientSound.start();
            }
        });
        ambientSound.start();

    }

    public void playMusic() {
        currentClip = musicClips.get(currentMusicIndex);
        FloatControl ctrl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
        ctrl.setValue(volume);
        System.out.println("Starting " + currentClip + " index " + currentMusicIndex);
        currentClip.start();
    }

    public void skipTrack() {
        currentClip.stop();
    }

    private void playNextTrack(LineEvent e) {

        if (e.getType() == LineEvent.Type.STOP) {
            currentMusicIndex++;
            if (currentMusicIndex > musicClips.size() - 1)
                currentMusicIndex = 0;
            currentClip.setMicrosecondPosition(0);
            currentClip.removeLineListener(this::playNextTrack);
            playMusic();
        }

    }
}
