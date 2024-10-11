package musicplayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.image.BufferedImage;

public class MusicPlayerWithLyrics {

    private Clip clip;
    private JLabel lyricLabel;
    private JLabel imageLabel; // Label for the image
    private JLabel timestampLabel; // Label for the timestamp
    private JSlider volumeSlider; // Slider for volume control
    private Timer lyricUpdater;

    public static void main(String[] args) {
        new MusicPlayerWithLyrics().createGUI();
    }

    public void createGUI() {
        JFrame frame = new JFrame("Thats what you get-Paramore");
        frame.setSize(400, 600); // Set width and height
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false); // Make the frame non-resizable

        // Center the frame on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.BLACK); 
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        // Label for displaying the image
        imageLabel = new JLabel();
        imageLabel.setBounds(75, 20, 250, 250); 
        panel.add(imageLabel);

        // Load and set the image
        try {
            BufferedImage img = ImageIO.read(new File("assets/paramore.jpg")); 
            ImageIcon icon = new ImageIcon(img.getScaledInstance(250, 250, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Label for displaying lyrics
        lyricLabel = new JLabel("Instrumental", SwingConstants.CENTER);
        lyricLabel.setBounds(25, 300, 350, 30); 
        lyricLabel.setForeground(Color.WHITE); 
        panel.add(lyricLabel);

        // Label for displaying timestamp
        timestampLabel = new JLabel("00:00", SwingConstants.CENTER);
        timestampLabel.setBounds(150, 350, 100, 30); 
        timestampLabel.setForeground(Color.WHITE); 
        panel.add(timestampLabel);

        // Volume Slider
        volumeSlider = new JSlider(0, 100, 100); 
        volumeSlider.setBounds(75, 400, 250, 40); 
        volumeSlider.setBackground(Color.BLACK); 
        volumeSlider.setForeground(Color.WHITE); 
        panel.add(volumeSlider);

        // Play Button
        JButton playButton = new JButton("Play Song");
        playButton.setBounds(75, 460, 100, 40); 
        playButton.setBackground(Color.ORANGE); 
        playButton.setForeground(Color.WHITE); 
        panel.add(playButton);

        // Stop Button
        JButton stopButton = new JButton("Stop Song");
        stopButton.setBounds(225, 460, 100, 40); // Positioned beside the Play button
        stopButton.setBackground(Color.ORANGE); // Set button background color
        stopButton.setForeground(Color.WHITE); // Set text color to white
        panel.add(stopButton);

        // ActionListener for play button
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playMusic("assets/Thats what you get.wav");
            }
        });

        // ActionListener for stop button
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopMusic();
            }
        });

        // ChangeListener for volume slider
        volumeSlider.addChangeListener(e -> {
            if (clip != null) {
                setVolume(volumeSlider.getValue() / 100.0f); // Set volume based on slider value
            }
        });
    }

    public void playMusic(String filePath) {
        try {
            if (clip != null && clip.isRunning()) {
                return; // Prevent multiple play instances
            }

            File musicFile = new File(filePath);
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(musicFile));
            clip.start();

            // Start Timer to update lyrics and timestamp
            lyricUpdater = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    long currentTime = clip.getMicrosecondPosition() / 1000; // Convert to ms
                    String currentLyric = getLyricAtTimestamp(currentTime);
                    lyricLabel.setText(currentLyric);

                    // Update timestamp label
                    long seconds = (currentTime / 1000) % 60;
                    long minutes = (currentTime / 1000) / 60;
                    timestampLabel.setText(String.format("%02d:%02d", minutes, seconds));
                }
            });
            lyricUpdater.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop(); // Stop the music
            clip.close(); // Close the clip
        }
        if (lyricUpdater != null) {
            lyricUpdater.stop(); // Stop lyric updates
        }
        lyricLabel.setText("Instrumental"); // Reset lyrics label
        timestampLabel.setText("00:00"); // Reset timestamp
    }

    public void setVolume(float volume) {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (20 * Math.log10(volume)); // Convert volume to decibels
            gainControl.setValue(dB); // Set volume
        }
    }


    public String getLyricAtTimestamp(long currentTime) {
        // Array of lyrics
        String[] lyrics = {
            "Instrumental",    // [00:00.00]
            "No sir, well, I don't wanna be the blame, not anymore", // [00:12.50]
            "It's your turn, so take a seat, we're settling the final score", // [00:17.18]
            "And why do we like to hurt so much?", // [00:23.66]
            "I can't decide", // [00:31.24]
            "You have made it harder just to go on", // [00:34.15]
            "And why, all the possibilities, well I was wrong", // [00:38.12]
            "Instrumental", // [00:44.00]
            "That's what you get when you let your heart win, whoa1x", // [00:44.36]
            "That's what you get when you let your heart win, whoa2x", // [00:50.26]
            "I drowned out all my sense with the sound of its beating", // [00:55.64]
            "And that's what you get when you let your heart win, whoa", // [01:01.69]
            "Instrumental", // [01:06.76]
            "I wonder, how am I supposed to feel when you're not here", // [01:07.03]
            "'Cause I burned every bridge I ever built when you were here", // [01:13.44]
            "I still try holding onto silly things, I never learn", // [01:17.93]
            "Oh why, all the possibilities, I'm sure you've heard", // [01:25.29]
            "Instrumental", // [01:31.63]
            "That's what you get when you let your heart win, whoa1x", // [01:31.89]
            "That's what you get when you let your heart win, whoa2x", // [01:37.97]
            "I drowned out all my sense with the sound of its beating", // [01:43.47]
            "And that's what you get when you let your heart win, whoa", // [01:49.56]
            "Pain make your way to me (to me)", // [01:57.03]
            "And I'll always be just so inviting", // [02:01.59]
            "If I ever start to think straight", // [02:06.08]
            "This heart will start a riot in me, let's start, start, hey!", // [02:09.18]
            "Why do we like to hurt so much?", // [02:19.31]
            "Oh why do we like to hurt so much?", // [02:24.27]
            "That's what you get when you let your heart win, whoa1x", // [02:30.06]
            "That's what you get when you let your heart win, whoa2x", // [02:30.36]
            "That's what you get when you let your heart win, whoa3x", // [02:36.15]
            "I can't trust myself with anything but this", // [02:42.35]
            "And that's what you get when you let your heart win, whoa", // [02:48.34]
            
        };

     // Array of timestamps
        long[] timestamps = {
            0,      // [00:00.00] Instrumental
            11500,  // [00:12.50] No sir, well, I don't wanna be the blame, not anymore
            17180,  // [00:17.18] It's your turn, so take a seat, we're settling the final score
            22660,  // [00:23.66] And why do we like to hurt so much?
            27240,  // [00:31.24] I can't decide
            29150,  // [00:34.15] You have made it harder just to go on
            33120,  // [00:38.12] And why, all the possibilities, well I was wrong
            39000,  // [00:44.00] Instrumental
            42360,  // [00:44.36] That's what you get when you let your heart win, whoa
            49260,  // [00:50.26] That's what you get when you let your heart win, whoa
            57640,  // [00:55.64] I drowned out all my sense with the sound of its beating
            64690,  // [01:01.69] And that's what you get when you let your heart win, whoa
            72560,  // [01:06.76] Instrumental
            82030,  // [01:07.03] I wonder, how am I supposed to feel when you're not here
            88440,  // [01:13.44] 'Cause I burned every bridge I ever built when you were here
            93930,  // [01:18.93] I still try holding onto silly things, I never learn
            99000,  // [01:26.29] Oh why, all the possibilities, I'm sure you've heard
            105530,  // [01:32.63] Instrumental
            109400,  // [01:32.89] That's what you get when you let your heart win, whoa
            113970,  // [01:38.97] That's what you get when you let your heart win, whoa
            123000, // [01:44.70] I drowned out all my sense with the sound of its beating
            130960, // [01:50.60] And that's what you get when you let your heart win, whoa
            138503, // [01:57.76] Pain make your way to me (to me)
            143159, // [02:01.59] And I'll always be just so inviting
            148080, // [02:06.08] If I ever start to think straight
            153718, // [02:11.18] This heart will start a riot in me, let's start, start, hey!
            160731, // [02:21.31] Why do we like to hurt so much?
            165827, // [02:26.27] Oh why do we like to hurt so much?
            170006, // [02:31.06] That's what you get when you let your heart win, whoa
            178036, // [02:31.36] That's what you get when you let your heart win, whoa
            185615, // [02:36.15] That's what you get when you let your heart win, whoa
            192235, // [02:41.35] I can't trust myself with anything but this
            199834, // [02:46.83] And that's what you get when you let your heart win, whoa
            
            
        };


        for (int i = timestamps.length - 1; i >= 0; i--) {
            if (currentTime >= timestamps[i]) {
                return lyrics[i];
            }
        }
        return "Instrumental"; // Default if no match
    }
}