package com.example.zenithchance.managers;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.zenithchance.models.Event;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.MultiFormatWriter;

/**
 * Class for QR code creation and interaction
 *
 * @author Emerson
 * @version 1.0
 */
public class QRManager {

    public QRManager() {
    }

    /**
     * Creates a QR bitmap of the inputted string to be populated in an ImageView
     *
     * @author Emerson
     * @param eventHash String of the unique event hash in FireBase
     * @return BitMap of a QR Code
     * Outside Sources:
     * https://www.geeksforgeeks.org/android/how-to-build-a-qr-code-android-app-using-firebase/
     */
    public Bitmap createQRBitMap(String eventHash) {
        String Gqr = eventHash;
        MultiFormatWriter writer = new MultiFormatWriter();

        // Tries creating the bitmap based off of the input string
        try {
            BitMatrix matrix = writer.encode(Gqr, BarcodeFormat.QR_CODE, 320,320);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(matrix);


        // Catches exceptions
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Method for updating an ImageView to display a QR code for an event
     *
     * @author Emerson
     * @param view ImageView of the fragment where the QR should be populated
     * @param event Event to be used as the QR link
     * Outside Sources:
     * https://www.geeksforgeeks.org/android/how-to-build-a-qr-code-android-app-using-firebase/
     */
    public void updateImageView(ImageView view, Event event) {
        Bitmap map = createQRBitMap("zenith1/" + event.getDocId());
        view.setImageBitmap(map);
        return;
    }


}
