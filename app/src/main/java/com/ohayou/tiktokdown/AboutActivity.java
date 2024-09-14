package com.ohayou.tiktokdown;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends AppCompatActivity {
  private LinearLayout linear1;
  private CircleImageView circleimageview1;
  private LinearLayout linear3;
  private MaterialButton button_back;
  private LinearLayout linear4;
  private LinearLayout linear5;
  private LinearLayout linear6;
  private TextView textview1;
  private TextView textview2;
  private LinearLayout linear7;
  private LinearLayout linear9;
  private LinearLayout linear10;
  private ImageView imageview2;
  private ImageView imageview3;
  private ImageView imageview4;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    initialize(savedInstanceState);
    initializeLogic();
  }

  private void initialize(Bundle _savedInstanceState) {
    linear1 = findViewById(R.id.linear1);
    circleimageview1 = findViewById(R.id.circleimageview1);
    linear3 = findViewById(R.id.linear3);
    linear4 = findViewById(R.id.linear4);
    linear5 = findViewById(R.id.linear5);
    linear6 = findViewById(R.id.linear6);
    textview1 = findViewById(R.id.textview1);
    textview2 = findViewById(R.id.textview2);
    linear7 = findViewById(R.id.linear7);
    linear9 = findViewById(R.id.linear9);
    linear10 = findViewById(R.id.linear10);
    imageview2 = findViewById(R.id.imageview2);
    imageview3 = findViewById(R.id.imageview3);
    imageview4 = findViewById(R.id.imageview4);
	button_back = findViewById(R.id.button_back);

    imageview2.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View _view) {
            Intent intent =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DitzOfc-Expertise"));
            startActivity(intent);
          }
        });

    imageview3.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View _view) {
            Intent intent =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@DitzOfc"));
            startActivity(intent);
          }
        });

    imageview4.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View _view) {
            Intent intent =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@ditz.ofc"));
            startActivity(intent);
          }
        });
    button_back.setOnClickListener(V -> {
		Intent intent = new Intent(AboutActivity.this, MainActivity.class);
		startActivity(intent);
	});
  }

  private void initializeLogic() {
    textview1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/poppins_medium.ttf"), 0);
    textview2.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/poppins_regular.ttf"), 0);
    _setCornerRadius(linear3, 20, 20, "#424242");
    _RippleEffect("#424242", imageview2);
    _RippleEffect("#424242", imageview3);
    _RippleEffect("#424242", imageview4);
  }

  public void _setCornerRadius(
      final View _view,
      final double _radius_strength,
      final double _shadow_line,
      final String _color) {
    android.graphics.drawable.GradientDrawable ab =
        new android.graphics.drawable.GradientDrawable();
    ab.setColor(Color.parseColor(_color));
    ab.setCornerRadius((float) _radius_strength);
    _view.setElevation((float) _shadow_line);
    _view.setBackground(ab);
  }

  public void _RippleEffect(final String _color, final View _view) {
    android.content.res.ColorStateList clr =
        new android.content.res.ColorStateList(
            new int[][] {new int[] {}}, new int[] {Color.parseColor(_color)});
    android.graphics.drawable.RippleDrawable ripdr =
        new android.graphics.drawable.RippleDrawable(clr, null, null);
    _view.setBackground(ripdr);
  }
}
