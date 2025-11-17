package tetris;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

public final class FileManager {

    /** Singleton instance of the class. */
    private static FileManager instance;
    /** Application logger. */
    private static Logger logger;
    /** Max number of high scores. */
    private static final int MAX_SCORES = 7;

    /**
     * private constructor.
     */
    private FileManager() {
    }

    /**
     * Returns shared instance of FileManager.
     *
     * @return Shared instance of FileManager.
     */
    protected static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    public Font loadFont(final float size) throws IOException,
            FontFormatException {
        InputStream inputStream = null;
        Font font;

        try {
            // Font loading.
            inputStream = FileManager.class.getClassLoader()
                    .getResourceAsStream("font.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(
                    size);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }

        return font;
    }
}

