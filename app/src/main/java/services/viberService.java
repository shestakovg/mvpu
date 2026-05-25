package services;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

public class viberService {
    public static void sharePdf(Context context, File pdfFile) {
        Uri pdfUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Invoice");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(intent, "Send invoice via...");
        context.startActivity(chooser);
    }

    public static void sendPdfViaViber(Context context, File pdfFile, String messageText ) {
        if (pdfFile == null || !pdfFile.exists()) {
            Toast.makeText(context, "PDF file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get URI via FileProvider (required for API 24+)
        Uri pdfUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
        intent.putExtra(Intent.EXTRA_SUBJECT, messageText);
        intent.putExtra(Intent.EXTRA_TEXT, "Please find attached invoice: " + messageText);
        intent.setPackage("com.viber.voip");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Viber is not installed", Toast.LENGTH_LONG).show();
            // Optional: open Play Store to install Viber
            openPlayStoreForViber(context);
        }
    }

    private static void openPlayStoreForViber(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.viber.voip")));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.viber.voip")));
        }
    }

    public static boolean isViberInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.viber.voip", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
