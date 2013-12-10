package app.cartoonify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.SeekBar;
import android.media.MediaScannerConnection;


public class ToonifyActivity extends Activity {

	private static final String TAG = "Cartoonify";

	private static final int SELECT_PICTURE = 1;
	private static final int GOTO_CAMERA = 2;

	private Bitmap mBitmapModified;
	private Bitmap mBitmap;
	private ProgressBar mProgBar;
	private TextView mInfoText;
	private boolean fProcess=false;
	
	private String mCurrentPhotoPath;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			if(status == LoaderCallbackInterface.SUCCESS) {
				Log.i(TAG, "OpenCV loaded successfully.");
			} else {
				super.onManagerConnected(status);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toonify);
		mProgBar = (ProgressBar) findViewById(R.id.prog_bar);
		mProgBar.setVisibility(View.INVISIBLE);
		mInfoText = (TextView) findViewById(R.id.info_message);
		
		SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar1); 
	    final TextView seekBarValue = (TextView)findViewById(R.id.seekbarvalue); 

	    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 

	   @Override 
	   public void onProgressChanged(SeekBar seekBar, int progress, 
	     boolean fromUser) { 
	    // TODO Auto-generated method stub 
	    seekBarValue.setText(String.valueOf(progress)); 
	   } 

	   @Override 
	   public void onStartTrackingTouch(SeekBar seekBar) { 
	    // TODO Auto-generated method stub 
	   } 

	   @Override 
	   public void onStopTrackingTouch(SeekBar seekBar) { 
	    // TODO Auto-generated method stub 
	   } 
	       });
	    
	    SeekBar SeekBar04 = (SeekBar)findViewById(R.id.SeekBar04); 
	    final TextView TextView04 = (TextView)findViewById(R.id.TextView04); 

	    SeekBar04.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 

	   @Override 
	   public void onProgressChanged(SeekBar seekBar, int progress, 
	     boolean fromUser) { 
	    // TODO Auto-generated method stub 
		   TextView04.setText(String.valueOf(progress)); 
	   } 

	   @Override 
	   public void onStartTrackingTouch(SeekBar seekBar) { 
	    // TODO Auto-generated method stub 
	   } 

	   @Override 
	   public void onStopTrackingTouch(SeekBar seekBar) { 
	    // TODO Auto-generated method stub 
	   } 
	       });
	    
	    SeekBar SeekBar02 = (SeekBar)findViewById(R.id.SeekBar02); 
	    final TextView TextView02 = (TextView)findViewById(R.id.TextView02); 

	    SeekBar02.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 

	   @Override 
	   public void onProgressChanged(SeekBar seekBar, int progress, 
	     boolean fromUser) { 
	    // TODO Auto-generated method stub 
		   TextView02.setText(String.valueOf(progress)); 
	   } 

	   @Override 
	   public void onStartTrackingTouch(SeekBar seekBar) { 
	    // TODO Auto-generated method stub 
	   } 

	   @Override 
	   public void onStopTrackingTouch(SeekBar seekBar) { 
	    // TODO Auto-generated method stub 
	   } 
	       });
	    
	    SeekBar SeekBar03 = (SeekBar)findViewById(R.id.SeekBar03); 
	    final TextView TextView03 = (TextView)findViewById(R.id.TextView03); 

	    SeekBar03.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ 

	   @Override 
	   public void onProgressChanged(SeekBar seekBar, int progress, 
	     boolean fromUser) { 
	    // TODO Auto-generated method stub 
		   TextView03.setText(String.valueOf(progress)); 
	   } 

	   @Override 
	   public void onStartTrackingTouch(SeekBar seekBar) { 
	    // TODO Auto-generated method stub 
	   } 

	   @Override 
	   public void onStopTrackingTouch(SeekBar seekBar) { 
	    // TODO Auto-generated method stub 
	   } 
	       });
	   
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		ImageView imview = (ImageView) findViewById(R.id.image_view);
		if(mBitmap != null && fProcess) {
			imview.setImageBitmap(mBitmap);
			mInfoText.setText(R.string.info_processing);
			mProgBar.setVisibility(View.VISIBLE);
			BitmapProcessTask bpt = new BitmapProcessTask(imview, mProgBar, mInfoText);
			mBitmapModified = mBitmap.copy(Bitmap.Config.RGB_565,false);
			bpt.execute(mBitmapModified);
			fProcess = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toonify, menu);
		return true;
	}
	
	public void toonify(View view) {
		fProcess = true;
		onResume();
	}
	
	public void restore(View view) {
		ImageView imview = (ImageView) findViewById(R.id.image_view);
		imview.setImageBitmap(mBitmapModified);
	}

	public void dispatchGalleryIntent(View view) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
	}
	
	public void dispatchCameraIntent(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
		startActivityForResult(intent, GOTO_CAMERA);
	}
//	
//	public static boolean isIntentAvailable(Context context, String action) {
//	    final PackageManager packageManager = context.getPackageManager();
//	    final Intent intent = new Intent(action);
//	    List<ResolveInfo> list =
//	            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//	    return list.size() > 0;
//	}
//	
//	private void handleSmallCameraPhoto(Intent intent) {
//	    Bundle extras = intent.getExtras();
//	    mImageBitmap = (Bitmap) extras.get("data");
//	    mImageView.setImageBitmap(mImageBitmap);
//	}

	public void onActivityResult(int requestCode, int resultCode, Intent data ) {
		if(resultCode == RESULT_OK) {
			if(requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				mBitmap = null;
				try {
					mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(mBitmap != null) {
					ImageView imview = (ImageView) findViewById(R.id.image_view);
					imview.setImageBitmap(mBitmap);
//					Log.i(TAG,"when is this happening");
//					mBitmapModified = mBitmap.copy(Bitmap.Config.RGB_565,false);
				}
			} else if(requestCode == GOTO_CAMERA) {
				 Log.i(TAG, "Ativity Completed");
				if (mCurrentPhotoPath != null) {
					 Log.i(TAG, "Taking Picture");
					setPic();
					Log.i(TAG, "setPic Done");
					galleryAddPic();
					Log.i(TAG, "gallery added");
					mCurrentPhotoPath = null;
				}
			}
		}
	}
	
	public void saveImage(View view) { 
		SeekBar seek = (SeekBar) findViewById(R.id.seekBar1);
	    int seekValue = seek.getProgress();
	    Log.i(TAG, "Seeking "+seekValue);
		ImageView imview = (ImageView) findViewById(R.id.image_view);
		if(imview != null) {
			
		}
		
		if(mBitmap != null) {
			Log.i(TAG, "saving image to gallery");
			saveImageToGallery(mBitmapModified);
		}
	}
	
	/*
	 * Used to save bitmaps and put them in the photo gallery.
	 */
	private void saveImageToGallery(Bitmap finalBitmap) {

	    String root = Environment.getExternalStorageDirectory().getAbsolutePath();
	    File myDir = new File(root + "/Cartoonified");    
	    myDir.mkdirs();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
	    String timestamp = sdf.format(new Date());
	    String fname = "Cartoonify-"+ timestamp +".jpg";
	    File file = new File (myDir, fname);
	    if (file.exists ()) file.delete (); 
	    try {
	           FileOutputStream out = new FileOutputStream(file);
	           finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
	           out.flush();
	           out.close();
	           mInfoText.setText("Saved: " + fname);
	    } catch (Exception e) {
	           e.printStackTrace();
	    }
	    MediaScannerConnection.scanFile(this,
	            new String[] { file.toString() }, null,
	            new MediaScannerConnection.OnScanCompletedListener() {
	        public void onScanCompleted(String path, Uri uri) {
	            Log.i("ExternalStorage", "Scanned " + path + ":");
	            Log.i("ExternalStorage", "-> uri=" + uri);
	        }
	   });
//	    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}
	
	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		ImageView mImageView = (ImageView) findViewById(R.id.image_view);
		
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		mBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//		Log.i(TAG,"when is this happening");
//		mBitmapModified = Bitmap.createBitmap(mBitmap);
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(mBitmap);
		mImageView.setVisibility(View.VISIBLE);
	}
	
	
	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String root = Environment.getExternalStorageDirectory().getAbsolutePath();
	    File myDir = new File(root + "/Cartoonified");    
	    myDir.mkdirs();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
	    String timestamp = sdf.format(new Date());
	    String fname = "Cartoonify-"+ timestamp +".jpg";
	    File file = new File (myDir, fname);
		return file;
	}
	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}
	
	private class BitmapProcessTask extends AsyncTask<Bitmap, Void, Bitmap> {
		private final WeakReference<ImageView> imviewRef;
		private final WeakReference<ProgressBar> progBarRef;
		private final WeakReference<TextView> textViewRef;

		public BitmapProcessTask(ImageView imview, ProgressBar progBar, TextView textView) {
			imviewRef = new WeakReference<ImageView>(imview);
			progBarRef = new WeakReference<ProgressBar>(progBar);
			textViewRef = new WeakReference<TextView>(textView);
		}

		@Override
		protected Bitmap doInBackground(Bitmap... params) {
			
			Bitmap bmp = params[0];
			
			
			Mat rgba = new Mat();
			Mat rgb = new Mat();
			Mat gray = new Mat();
			
			Mat smallrgb = new Mat();
			Mat edges = new Mat();
			Mat tempA1 = new Mat();
			Mat tempA2 = new Mat();
			Mat tempB1 = new Mat();
			
			Mat result = new Mat();
			
			Utils.bitmapToMat(bmp, rgba);
			Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGBA2GRAY);
			Imgproc.cvtColor(rgba, rgb, Imgproc.COLOR_RGBA2RGB);
			
			Log.i(TAG, "size: " + gray.size().toString());
			TextView text = (TextView)findViewById(R.id.seekbarvalue); 
			Integer MedFilterParam = Integer.parseInt(text.getText().toString());
			if(MedFilterParam%2==0) MedFilterParam--;
			Log.i(TAG, "Median Filter Parameter = "+MedFilterParam);
			Imgproc.medianBlur(gray, tempA1, MedFilterParam);
			
			Imgproc.Canny(tempA1, edges, 50, 90);
			
			Size dilateSize = new Size(2,2);
			Imgproc.dilate(edges, edges, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, dilateSize));
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Imgproc.findContours(edges, contours, tempA2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
			Log.i(TAG, "num contours: " + contours.size());
			List<MatOfPoint> keepContours = new ArrayList<MatOfPoint>();
			
			text = (TextView)findViewById(R.id.TextView04); 
			Integer EdgeFilterthreshold = Integer.parseInt(text.getText().toString());
			
			Log.i(TAG, "Edge Filter Threshold = "+EdgeFilterthreshold);
			
			for(MatOfPoint contour : contours) {
				if(Imgproc.contourArea(contour) > EdgeFilterthreshold)
					keepContours.add(contour);
			}
			
			double scale = 4;
			Size smallSize = new Size(rgb.size().width/scale, rgb.size().height/scale);
			Imgproc.resize(rgb, smallrgb, smallSize);
			
			
			text = (TextView)findViewById(R.id.TextView02); 
			Integer BFReps = Integer.parseInt(text.getText().toString());
			
			Log.i(TAG, "Bilateral Filter Repetitions = "+BFReps);
			
			text = (TextView)findViewById(R.id.TextView03); 
			Integer BFSize = Integer.parseInt(text.getText().toString());
			
			Log.i(TAG, "Bilateral Filter Kernel Size = "+BFSize);
			 
			double sigmaC = 15; 
			double sigmaS = 7; 
			for(int i = 0; i < BFReps; i++) {
				Imgproc.bilateralFilter(smallrgb, tempB1, BFSize, sigmaC, sigmaS);
				Imgproc.bilateralFilter(tempB1, smallrgb, BFSize, sigmaC, sigmaS);
			}
			
			Imgproc.resize(smallrgb, rgb, rgba.size(), 0, 0, Imgproc.INTER_LINEAR);
			
			Imgproc.medianBlur(rgb, rgb, 7);
			
			Size size = rgb.size();
			double colorScale = 24; 
			byte[] pixels = new byte[3];
			Log.i(TAG, CvType.typeToString(rgb.type()));	
			for(int row = 0; row < size.height; row++) {
				for(int col = 0; col < size.width; col++) {
					rgb.get(row, col, pixels);
					for(int i = 0; i < 3; i++) {
						int pix = (int)pixels[i];
						pix = (int)(Math.floor((pix) / colorScale) * colorScale);
						if(pix < -128) pix = -128;
						if(pix > 127) pix = 127;
						pixels[i] = (byte)pix;
					}
					rgb.put(row, col, pixels);
				}
			}
			Imgproc.medianBlur(rgb, rgb, 11);
			
			Imgproc.drawContours(rgb, keepContours, -1, new Scalar(30,30,30), -1);
			
			Imgproc.cvtColor(rgb, result, Imgproc.COLOR_RGB2RGBA);
			
			Utils.matToBitmap(result, bmp);
			
			rgba.release();
			rgb.release();
			gray.release();
			smallrgb.release();
			edges.release();
			tempA1.release();
			tempA2.release();
			tempB1.release();
			result.release();
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if(isCancelled()) {
				bitmap = null;
			}
			if(imviewRef != null) {
				ImageView imview = imviewRef.get();
				if(imview != null) {
					Log.i(TAG, "setting the bitmap now...");
					imview.setImageBitmap(bitmap);
				}
			}
			if(progBarRef != null) {
				ProgressBar progBar = progBarRef.get();
				if(progBar != null) {
					progBar.setVisibility(View.INVISIBLE);
				}
			}
			if(textViewRef != null) {
				TextView textView = textViewRef.get();
				if(textView != null) {
					textView.setText(R.string.info_process_complete);
				}
			}
		}

	}
}
