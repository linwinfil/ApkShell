package my.beautycamera;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity
{
    private static final String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button button = findViewById(R.id.add_shortcut_button);
        button.setOnClickListener(v -> addShortCut());

        Button button2 = findViewById(R.id.remove_shortcut_button);
        button2.setOnClickListener(v -> removeShortCut());
    }

    void addShortCut()
    {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, "goto_web")
                .setShortLabel("web site")
                .setLongLabel("open the web site")
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher_round))
                .setIntent(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.zhihu.com")
                )).build();
        if (shortcutManager != null)
        {
            boolean success = shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo));
            Log.d(TAG, "Main2Activity --> addShortCut: " + success);
        }
    }

    void removeShortCut()
    {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        if (shortcutManager != null)
        {
            shortcutManager.removeAllDynamicShortcuts();
        }
    }
}
