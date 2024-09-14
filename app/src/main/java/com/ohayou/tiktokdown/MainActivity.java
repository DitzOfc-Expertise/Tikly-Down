package com.ohayou.tiktokdown;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText; 
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    private LinearLayout linear_utama;
    private TextInputLayout textinputlayout1;
    private EditText edittext_url;
    private BottomNavigationView bottomnavigation1;
    private MaterialButton button1;
    private LinearLayout videoBox;
    private ImageView videoThumbnail;
    private TextView videoTitle;
    private TextView videoAuthor;
    private MaterialButton videoDownloadButton;
    private LinearLayout audioBox;
    private ImageView audioThumbnail;
    private TextView audioTitle;
    private TextView audioAuthor;
    private MaterialButton audioDownloadButton;
    private ProgressDialog progressDialog;
    private JSONObject responseData;
	private TextView text_title_more;
	private MaterialButton button_about;
	private MaterialButton button_docs;
	private MaterialButton button_donate;
	
    // New variables for slide feature
	// @noDeprecated
    private ViewPager2 slideViewPager;
    private LinearLayout indicatorLayout;
    private MaterialButton downloadAllSlidesButton;
    private List<String> slideUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        initializeLogic();
    }
		
    private void initialize() {
        linear_utama = findViewById(R.id.linear_utama);
        bottomnavigation1 = findViewById(R.id.bottomnavigation1);
        textinputlayout1 = findViewById(R.id.textinputlayout1);
        button1 = findViewById(R.id.button1);
        edittext_url = findViewById(R.id.edittext_url);
        videoBox = findViewById(R.id.video_box);
        videoThumbnail = findViewById(R.id.video_thumbnail);
        videoTitle = findViewById(R.id.video_title);
        videoAuthor = findViewById(R.id.video_author);
        videoDownloadButton = findViewById(R.id.video_download_button);
        audioBox = findViewById(R.id.audio_box);
        audioThumbnail = findViewById(R.id.audio_thumbnail);
        audioTitle = findViewById(R.id.audio_title);
        audioAuthor = findViewById(R.id.audio_author);
        audioDownloadButton = findViewById(R.id.audio_download_button);
       // progressDialog = new ProgressDialog(this);
       // progressDialog.setMessage("Downloading...");
       // progressDialog.setCancelable(false);

        // Initialize new UI elements for slide feature
        slideViewPager = findViewById(R.id.slideViewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        downloadAllSlidesButton = findViewById(R.id.downloadAllSlidesButton);
		button_about = findViewById(R.id.button_about);
		button_docs = findViewById(R.id.button_docs);
		button_donate = findViewById(R.id.button_donate);
		
        button1.setOnClickListener(v -> handleDownload());
        videoDownloadButton.setOnClickListener(v -> handleVideoDownload());
        audioDownloadButton.setOnClickListener(v -> handleAudioDownload());
        downloadAllSlidesButton.setOnClickListener(v -> handleDownloadAllSlides());
		button_about.setOnClickListener(V -> {
			Intent intent = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(intent);	
		});
		button_docs.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View _view) {
            Intent intent =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.tiklydown.eu.org"));
            startActivity(intent);
          }
        });
		button_donate.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View _view) {
            Intent intent =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://saweria.co/DitzOfc"));
            startActivity(intent);
          }
        });
    }
	
	private void onLoading() {
	    findViewById(R.id.indicator_progress).setVisibility(View.VISIBLE);  
    }	
	
	private void onCompleteLoading() {
		findViewById(R.id.indicator_progress).setVisibility(View.GONE);
	}
	
	private void showMore() {		
		findViewById(R.id.linear_docs).setVisibility(View.VISIBLE);
		findViewById(R.id.linear_utama).setVisibility(View.GONE);
	}
	
    private void initializeLogic() {
    
    bottomnavigation1.getMenu().add(0, 0, 0, "Home").setIcon(R.drawable.ic_home);
    bottomnavigation1.getMenu().add(0, 1, 0, "More").setIcon(R.drawable.ic_more);

    bottomnavigation1.setOnItemSelectedListener(item -> {
        switch (item.getItemId()) {
            case 0:
                findViewById(R.id.linear_docs).setVisibility(View.GONE);
                findViewById(R.id.linear_utama).setVisibility(View.VISIBLE);
                return true;
            case 1:
                showMore();
                return true;
            default:
                return false;
         }
      });
    }
	
	private void setupSlideViewPager() {
      SlideAdapter adapter = new SlideAdapter(slideUrls);
      slideViewPager.setAdapter(adapter);

      setupIndicators();
      setCurrentIndicator(0);

    slideViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            setCurrentIndicator(position);
        }
      });

      slideViewPager.setVisibility(View.VISIBLE);
      indicatorLayout.setVisibility(View.VISIBLE);
      downloadAllSlidesButton.setVisibility(View.VISIBLE);
   }

    private void setupIndicators() {
      indicatorLayout.removeAllViews();
      ImageView[] indicators = new ImageView[slideUrls.size()];
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      layoutParams.setMargins(8, 0, 8, 0);
      for (int i = 0; i < indicators.length; i++) {
        indicators[i] = new ImageView(this);
        indicators[i].setImageDrawable(getResources().getDrawable(R.drawable.indicator_inactive));
        indicators[i].setLayoutParams(layoutParams);
        indicatorLayout.addView(indicators[i]);
      }
   }
   
    private void setCurrentIndicator(int position) {
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.indicator_active));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.indicator_inactive));
            }
        }
    }
    private void handleDownload() {
        String url = edittext_url.getText().toString().trim();

        if (url.isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Input URL!")
                .setPositiveButton(android.R.string.ok, null)
                .show();
            return;
        }

        onLoading();
        new Thread(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL apiUrl = new URL("https://api.tiklydown.eu.org/api/download?url=" + Uri.encode(url));
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                Log.d("HTTP", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("HTTP", "Response: " + result.toString());

                    runOnUiThread(() -> {
                        onCompleteLoading();
                        parseResponse(result.toString());
                    });
                } else {
                    Log.e("HTTP", "Error: " + responseCode);
                    runOnUiThread(() -> {
                        onCompleteLoading();
                        new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Failed to connect to API")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                    });
                }
            } catch (Exception e) {
                Log.e("HTTP", "Exception: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    onCompleteLoading();
                    new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Failed to download")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                });
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("HTTP", "Reader close error: " + e.getMessage(), e);
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void parseResponse(String json) {
		try {
			responseData = new JSONObject(json);
			JSONObject video = responseData.optJSONObject("video");
			JSONObject music = responseData.optJSONObject("music");
			JSONObject author = responseData.optJSONObject("author");
			JSONArray images = responseData.optJSONArray("images");
			
			if (images != null && images.length() > 0) {
                slideUrls = new ArrayList<>();
                for (int i = 0; i < images.length(); i++) {
                    JSONObject image = images.getJSONObject(i);
                    String imageUrl = image.optString("url");
                    if (!imageUrl.isEmpty()) {
                        slideUrls.add(imageUrl);
                    }
                }
                if (!slideUrls.isEmpty()) {
                    setupSlideViewPager();
                }
            } else {
                slideViewPager.setVisibility(View.GONE);
                indicatorLayout.setVisibility(View.GONE);
                downloadAllSlidesButton.setVisibility(View.GONE);
            }
 
            if (video != null && !video.optString("noWatermark").isEmpty()) {
                videoBox.setVisibility(View.VISIBLE);
                String videoUrl = video.optString("cover");
                videoTitle.setText(responseData.optString("title"));
                videoAuthor.setText(author.optString("name"));
                loadImage(videoUrl, videoThumbnail);
            } else {
                videoBox.setVisibility(View.GONE);
            }

            if (music != null && !music.optString("play_url").isEmpty()) {
                audioBox.setVisibility(View.VISIBLE);
                String audioUrl = music.optString("cover_large");
                audioTitle.setText(music.optString("title"));
                audioAuthor.setText(music.optString("author"));
                loadImage(audioUrl, audioThumbnail);
            } else {
                audioBox.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            Log.e("JSON", "Error parsing JSON: " + e.getMessage(), e);
        }
    }

    private void loadImage(String url, ImageView imageView) {
        new Thread(() -> {
            try {
                InputStream in = new URL(url).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            } catch (Exception e) {
                Log.e("Image", "Error loading image: " + e.getMessage(), e);
            }
        }).start();
    }

    private void handleVideoDownload() {
		if (responseData == null) {
			showErrorDialog("No video data available");
			return;
		}
		
		try {
			JSONObject video = responseData.getJSONObject("video");
			String videoUrl = video.getString("noWatermark");
			String fileName = "TikTok_Video_" + System.currentTimeMillis() + ".mp4";
			downloadFile(videoUrl, fileName, "video/mp4");
			} catch (JSONException e) {
			Log.e("JSON", "Error parsing video JSON: " + e.getMessage(), e);
			showErrorDialog("Failed to get video download link");
		}
	}
	
    private void handleAudioDownload() {
        if (responseData == null) {
            showErrorDialog("No audio data available");
            return;
        }

        try {
            JSONObject music = responseData.getJSONObject("music");
            String audioUrl = music.getString("play_url");
            String fileName = "TikTok_Audio_" + System.currentTimeMillis() + ".mp3";
            downloadFile(audioUrl, fileName, "audio/mpeg");
        } catch (JSONException e) {
            Log.e("JSON", "Error parsing audio JSON: " + e.getMessage(), e);
            showErrorDialog("Failed to get audio download link");
        }
    }

    
	 private void handleDownloadAllSlides() {
        for (String url : slideUrls) {
            downloadSlide(url);
        }
    }

    private void downloadSlide(String url) {
        String fileName = "Slide_" + System.currentTimeMillis() + ".jpg";
        downloadFile(url, fileName, "image/jpeg");
    }

    private void downloadFile(String url, String fileName, String mimeType) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(fileName);
        request.setMimeType(mimeType);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
            showSuccessDialog("Succesfully saved!");
        } else {
            showErrorDialog("Download manager not available");
        }
    }

    private void showErrorDialog(String message) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
        });
    }

    private void showSuccessDialog(String message) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
        });
    }
	
    private class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {
     private List<String> slideUrls;

     SlideAdapter(List<String> slideUrls) {
        this.slideUrls = slideUrls;
     }

      @NonNull
      @Override
      public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item, parent, false);
        return new SlideViewHolder(view);
      }

      @Override
      public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
          String url = slideUrls.get(position);
          Glide.with(MainActivity.this)
            .load(url)
            .into(holder.slideImage);
          holder.slideCaption.setText("Slide " + (position + 1));
          holder.slideDownloadButton.setOnClickListener(v -> downloadSlide(url));
       }


       @Override
       public int getItemCount() {
        return slideUrls.size();
      }

      class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView slideImage;
        TextView slideCaption;
        MaterialButton slideDownloadButton;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            slideImage = itemView.findViewById(R.id.slideImage);
            slideCaption = itemView.findViewById(R.id.slideCaption);
            slideDownloadButton = itemView.findViewById(R.id.slideDownloadButton);
        }
     }
  }
}