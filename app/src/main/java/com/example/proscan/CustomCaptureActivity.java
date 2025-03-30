package com.example.proscan;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class CustomCaptureActivity extends CaptureActivity {
    private DecoratedBarcodeView barcodeScannerView;
    private ImageView scanFrame;
    private TextView scanText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        scanFrame = findViewById(R.id.scan_frame);
        scanText = findViewById(R.id.scan_text);

        // 设置扫码回调
        barcodeScannerView.decodeSingle(result -> {
            // 扫码成功后的处理
            setResult(RESULT_OK, getIntent().putExtra("SCAN_RESULT", result.getText()));
            
            // 添加震动效果
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(300); // 震动300毫秒
            }
            
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }
}