package com.example.proscan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proscan.db.HistoryDbHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.app.Dialog;
import android.widget.ImageView;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUrl;
    private Button buttonScan;
    private Button buttonPaste;
    private Button btnVisit;
    private ImageButton buttonHistory;
    private HistoryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new HistoryDbHelper(this);
        editTextUrl = findViewById(R.id.editTextUrl);
        buttonScan = findViewById(R.id.buttonScan);
        buttonPaste = findViewById(R.id.buttonPaste);
        btnVisit = findViewById(R.id.btnVisit);
        buttonHistory = findViewById(R.id.buttonHistory);

        buttonScan.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("请将二维码放入框内扫描");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(false);
            integrator.setCaptureActivity(CustomCaptureActivity.class);
            integrator.initiateScan();
        });

        buttonPaste.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                String text = item.getText().toString();
                editTextUrl.setText(text);
                dbHelper.addHistoryItem(text, "paste");
                Toast.makeText(this, "已粘贴", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "剪贴板为空", Toast.LENGTH_SHORT).show();
            }
        });

        btnVisit.setOnClickListener(v -> {
            String url = editTextUrl.getText().toString().trim();
            if (!url.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "无法打开链接", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "请输入链接", Toast.LENGTH_SHORT).show();
            }
        });

        buttonHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        Button btnBarcode = findViewById(R.id.btnBarcode);
        Button btnQRCode = findViewById(R.id.btnQRCode);

        btnBarcode.setOnClickListener(v -> generateBarcode());
        btnQRCode.setOnClickListener(v -> generateQRCode());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "扫描已取消", Toast.LENGTH_SHORT).show();
            } else {
                String content = result.getContents();
                editTextUrl.setText(content);
                dbHelper.addHistoryItem(content, "scan");
                Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void generateBarcode() {
        String content = editTextUrl.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, 800, 200);
            Bitmap bitmap = Bitmap.createBitmap(800, 200, Bitmap.Config.ARGB_8888);
            
            for (int x = 0; x < 800; x++) {
                for (int y = 0; y < 200; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            showBarcodeDialog(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "生成条形码失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateQRCode() {
        String content = editTextUrl.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
            
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            
            showQRCodeDialog(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "生成二维码失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBarcodeDialog(Bitmap bitmap) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        
        dialog.setContentView(imageView);
        dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showQRCodeDialog(Bitmap bitmap) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        
        dialog.setContentView(imageView);
        dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        
        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
} 