package com.dcmmoguls.hardinglaw;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.desmond.squarecamera.CameraActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.dcmmoguls.hardinglaw.R.id.textView;


public class NewInjuryActivity extends AppCompatActivity {

    private Button btnPickLocation, btnPickDate, btnPickTime, btnPhoto1, btnPhoto2, btnPhoto3, btnInjuredPhoto1, btnInjuredPhoto2, btnPolicePhoto, btnSubmit;
    private EditText etLocation, etDate, etTime, etDriverName, etDriverPhone, etDriverLicense, etVehicle, etInsuranceCompany, etWitnessName1, etWitnessPhone1, etWitnessName2, etWitnessPhone2, etInjuredName1, etInjuredName2, etInjuredPhone1, etInjuredPhone2, etPoliceName, etPoliceNumber, etPoliceReportNumber, etYourName, etYourPhone, etYourAddress, etYourLicense;
    private ImageView imgPhoto1, imgPhoto2, imgPhoto3, imgPhoto4, imgPhoto5, imgPhoto6;
    private static Uri photoUri1, photoUri2, photoUri3, photoUri4, photoUri5, photoUri6;
    private static String gsPhotoReference1 = null, gsPhotoReference2 = null, gsPhotoReference3 = null, gsPhotoReference4 = null, gsPhotoReference5 = null, gsPhotoReference6 = null;
    private static Spanned spannedLocation = null;

    private int PLACE_PICKER_REQUEST = 2100;

    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReference();

    private SharedPreferences sharedPref;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_injury);

        sharedPref = getSharedPreferences("com.dcmmoguls.hardinglaw", Context.MODE_PRIVATE);
        uid = sharedPref.getString("userid", "");

        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.chat_actionbar,
                null);
        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        TextView tvTitle = (TextView) actionBarLayout.findViewById(R.id.tvTitle);
        tvTitle.setText("New Accident Report");
        // Set up your ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.topbarback));
        ImageButton btnback = (ImageButton) actionBarLayout.findViewById(R.id.btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPickLocation = (Button) findViewById(R.id.btnPickLocation);
        btnPickDate = (Button) findViewById(R.id.btnPickDate);
        btnPickTime = (Button) findViewById(R.id.btnPickTime);
        btnPhoto1 = (Button) findViewById(R.id.btnPhoto1);
        btnPhoto2 = (Button) findViewById(R.id.btnPhoto2);
        btnPhoto3 = (Button) findViewById(R.id.btnPhoto3);
        btnInjuredPhoto1 = (Button) findViewById(R.id.btnInjuredPhoto1);
        btnInjuredPhoto2 = (Button) findViewById(R.id.btnInjuredPhoto2);
        btnPolicePhoto = (Button) findViewById(R.id.btnPolicePhoto);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        etDriverName = (EditText) findViewById(R.id.etDriverName);
        etDriverPhone = (EditText) findViewById(R.id.etDriverPhone);
        etDriverLicense = (EditText) findViewById(R.id.etDriverLicense);
        etVehicle = (EditText) findViewById(R.id.etVehicle);
        etInsuranceCompany = (EditText) findViewById(R.id.etInsuranceCompany);
        etWitnessName1 = (EditText) findViewById(R.id.etWitnessName1);
        etWitnessName2 = (EditText) findViewById(R.id.etWitnessName2);
        etWitnessPhone1 = (EditText) findViewById(R.id.etWitnessPhone1);
        etWitnessPhone2 = (EditText) findViewById(R.id.etWitnessPhone2);
        etInjuredName1 = (EditText) findViewById(R.id.etInjuredName1);
        etInjuredName2 = (EditText) findViewById(R.id.etInjuredName2);
        etInjuredPhone1 = (EditText) findViewById(R.id.etInjuredPhone1);
        etInjuredPhone2 = (EditText) findViewById(R.id.etInjuredPhone2);
        etPoliceName = (EditText) findViewById(R.id.etPoliceName);
        etPoliceNumber = (EditText) findViewById(R.id.etPoliceNumber);
        etPoliceReportNumber = (EditText) findViewById(R.id.etPoliceReportNumber);
        etYourName = (EditText) findViewById(R.id.etYourName);
        etYourPhone = (EditText) findViewById(R.id.etYourPhoneNumber);
        etYourAddress = (EditText) findViewById(R.id.etYourAddress);
        etYourLicense = (EditText) findViewById(R.id.etYourLicense);
        imgPhoto1 = (ImageView) findViewById(R.id.imgPhoto1);
        imgPhoto2 = (ImageView) findViewById(R.id.imgPhoto2);
        imgPhoto3 = (ImageView) findViewById(R.id.imgPhoto3);
        imgPhoto4 = (ImageView) findViewById(R.id.imgPhoto4);
        imgPhoto5 = (ImageView) findViewById(R.id.imgPhoto5);
        imgPhoto6 = (ImageView) findViewById(R.id.imgPhoto6);

        View.OnClickListener datePickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                final Calendar mcurrentDate=Calendar.getInstance();
                int mYear=mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker=new DatePickerDialog(NewInjuryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        mcurrentDate.set(Calendar.YEAR, selectedyear);
                        mcurrentDate.set(Calendar.MONTH, selectedmonth);
                        mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);
                        DateFormat df = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                        String now = df.format(mcurrentDate);
                        etDate.setText(now);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        };

        etDate.setOnClickListener(datePickListener);
        btnPickDate.setOnClickListener(datePickListener);


        View.OnClickListener timePickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NewInjuryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String strTime = String.format("%02dh %02dm", selectedHour, selectedMinute);
                        etTime.setText(strTime);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        };

        etTime.setOnClickListener(timePickListener);
        btnPickTime.setOnClickListener(timePickListener);

        View.OnClickListener locationPickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationPick();
            }
        };

        etLocation.setOnClickListener(locationPickListener);
        btnPickLocation.setOnClickListener(locationPickListener);

        btnPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dispatchTakePictureIntent(1);
                Intent startCustomCameraIntent = new Intent(NewInjuryActivity.this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, 1);
            }
        });

        btnPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCustomCameraIntent = new Intent(NewInjuryActivity.this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, 2);
                //dispatchTakePictureIntent(2);
            }
        });

        btnPhoto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCustomCameraIntent = new Intent(NewInjuryActivity.this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, 3);
                //dispatchTakePictureIntent(2);
            }
        });

        btnInjuredPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCustomCameraIntent = new Intent(NewInjuryActivity.this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, 4);
                //dispatchTakePictureIntent(2);
            }
        });

        btnInjuredPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCustomCameraIntent = new Intent(NewInjuryActivity.this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, 5);
                //dispatchTakePictureIntent(2);
            }
        });

        btnPolicePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCustomCameraIntent = new Intent(NewInjuryActivity.this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, 6);
                //dispatchTakePictureIntent(2);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (photoUri1 != null)
            outState.putString("photoUri1", photoUri1.getPath());
        if (photoUri2 != null)
            outState.putString("photoUri2", photoUri2.getPath());
        if (photoUri3 != null)
            outState.putString("photoUri3", photoUri3.getPath());
        if (photoUri4 != null)
            outState.putString("photoUri4", photoUri4.getPath());
        if (photoUri5 != null)
            outState.putString("photoUri5", photoUri5.getPath());
        if (photoUri6 != null)
            outState.putString("photoUri6", photoUri6.getPath());

        if (gsPhotoReference1 != null)
            outState.putString("gsPhotoReference1", gsPhotoReference1);
        if (gsPhotoReference2 != null)
            outState.putString("gsPhotoReference2", gsPhotoReference2);
        if (gsPhotoReference3 != null)
            outState.putString("gsPhotoReference3", gsPhotoReference3);
        if (gsPhotoReference4 != null)
            outState.putString("gsPhotoReference4", gsPhotoReference4);
        if (gsPhotoReference5 != null)
            outState.putString("gsPhotoReference5", gsPhotoReference5);
        if (gsPhotoReference6 != null)
            outState.putString("gsPhotoReference6", gsPhotoReference6);


        if(spannedLocation != null) {
            outState.putString("etLocation", Html.toHtml(spannedLocation));
        }
        outState.putString("etDate", etDate.getText().toString());
        outState.putString("etTime", etTime.getText().toString());

        outState.putString("etDriverName", etDriverName.getText().toString());
        outState.putString("etDriverPhone", etDriverPhone.getText().toString());
        outState.putString("etDriverLicense", etDriverLicense.getText().toString());
        outState.putString("etVehicle", etVehicle.getText().toString());
        outState.putString("etInsuranceCompany", etInsuranceCompany.getText().toString());

        outState.putString("etWitnessName1", etWitnessName1.getText().toString());
        outState.putString("etWitnessPhone1", etWitnessPhone1.getText().toString());
        outState.putString("etWitnessName2", etWitnessName2.getText().toString());
        outState.putString("etWitnessPhone2", etWitnessPhone2.getText().toString());

        outState.putString("etInjuredName1", etInjuredName1.getText().toString());
        outState.putString("etInjuredPhone1", etInjuredPhone1.getText().toString());

        outState.putString("etInjuredName2", etInjuredName2.getText().toString());
        outState.putString("etInjuredPhone2", etInjuredPhone2.getText().toString());

        outState.putString("etPoliceName", etPoliceName.getText().toString());
        outState.putString("etPoliceNumber", etPoliceNumber.getText().toString());
        outState.putString("etPoliceReportNumber", etPoliceReportNumber.getText().toString());

        outState.putString("etYourName", etYourName.getText().toString());
        outState.putString("etYourPhone", etYourPhone.getText().toString());
        outState.putString("etYourAddress", etYourAddress.getText().toString());
        outState.putString("etYourLicense", etYourLicense.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey("photoUri1")) {
            photoUri1 = Uri.fromFile(new File(savedInstanceState.getString("photoUri1")));
            imgPhoto1.setImageURI(photoUri1);
        }
        if(savedInstanceState.containsKey("photoUri2")) {
            photoUri2 = Uri.fromFile(new File(savedInstanceState.getString("photoUri2")));
            imgPhoto2.setImageURI(photoUri2);
        }
        if(savedInstanceState.containsKey("photoUri3")) {
            photoUri3 = Uri.fromFile(new File(savedInstanceState.getString("photoUri3")));
            imgPhoto3.setImageURI(photoUri3);
        }
        if(savedInstanceState.containsKey("photoUri4")) {
            photoUri4 = Uri.fromFile(new File(savedInstanceState.getString("photoUri4")));
            imgPhoto4.setImageURI(photoUri4);
        }
        if(savedInstanceState.containsKey("photoUri5")) {
            photoUri5 = Uri.fromFile(new File(savedInstanceState.getString("photoUri5")));
            imgPhoto5.setImageURI(photoUri5);
        }
        if(savedInstanceState.containsKey("photoUri6")) {
            photoUri6 = Uri.fromFile(new File(savedInstanceState.getString("photoUri6")));
            imgPhoto6.setImageURI(photoUri6);
        }
        if(savedInstanceState.containsKey("gsPhotoReference1"))
            gsPhotoReference1 = savedInstanceState.getString("gsPhotoReference1");
        if(savedInstanceState.containsKey("gsPhotoReference2"))
            gsPhotoReference2 = savedInstanceState.getString("gsPhotoReference2");
        if(savedInstanceState.containsKey("gsPhotoReference3"))
            gsPhotoReference3 = savedInstanceState.getString("gsPhotoReference3");
        if(savedInstanceState.containsKey("gsPhotoReference4"))
            gsPhotoReference4 = savedInstanceState.getString("gsPhotoReference4");
        if(savedInstanceState.containsKey("gsPhotoReference5"))
            gsPhotoReference5 = savedInstanceState.getString("gsPhotoReference5");
        if(savedInstanceState.containsKey("gsPhotoReference6"))
            gsPhotoReference6 = savedInstanceState.getString("gsPhotoReference6");

        if(savedInstanceState.containsKey("etLocation"))
            etLocation.setText(Html.fromHtml(savedInstanceState.getString("etLocation")));
        etDate.setText(savedInstanceState.getString("etDate"));
        etTime.setText(savedInstanceState.getString("etTime"));

        etDriverName.setText(savedInstanceState.getString("etDriverName"));
        etDriverPhone.setText(savedInstanceState.getString("etDriverPhone"));
        etDriverLicense.setText(savedInstanceState.getString("etDriverLicense"));
        etVehicle.setText(savedInstanceState.getString("etVehicle"));
        etInsuranceCompany.setText(savedInstanceState.getString("etInsuranceCompany"));

        etWitnessName1.setText(savedInstanceState.getString("etWitnessName1"));
        etWitnessPhone1.setText(savedInstanceState.getString("etWitnessPhone1"));
        etWitnessName2.setText(savedInstanceState.getString("etWitnessName2"));
        etWitnessPhone2.setText(savedInstanceState.getString("etWitnessPhone2"));

        etInjuredName1.setText(savedInstanceState.getString("etInjuredName1"));
        etInjuredPhone1.setText(savedInstanceState.getString("etInjuredPhone1"));
        etInjuredName2.setText(savedInstanceState.getString("etInjuredName2"));
        etInjuredPhone2.setText(savedInstanceState.getString("etInjuredPhone2"));

        etPoliceName.setText(savedInstanceState.getString("etPoliceName"));
        etPoliceNumber.setText(savedInstanceState.getString("etPoliceNumber"));
        etPoliceReportNumber.setText(savedInstanceState.getString("etPoliceReportNumber"));

        etYourName.setText(savedInstanceState.getString("etYourName"));
        etYourPhone.setText(savedInstanceState.getString("etYourPhone"));
        etYourAddress.setText(savedInstanceState.getString("etYourAddress"));
        etYourLicense.setText(savedInstanceState.getString("etYourLicense"));
    }
/*
    private void dispatchTakePictureIntent(int nParam) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.dcmmoguls.hardinglaw.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, nParam);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap getPic(Uri uri){
        if(uri == null)
            return null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        // Determine how much to scale down the image
        // Decode the image file into a Bitmap sized to fill the View
        int scale = 50;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW/scale, photoH/scale);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        ExifInterface exif;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return rotatedBitmap;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }
*/
    private void uploadImage(Uri uri, final int nParam) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
        long startTime = calendar.getTimeInMillis();
        final StorageReference gsReference = storageRef.child(uid + "/" + startTime);

        UploadTask uploadTask = gsReference.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                ShortURL.makeShortUrl(downloadUrl.toString(), new ShortURL.ShortUrlListener() {
                    @Override
                    public void OnFinish(String url) {

                        if (url != null && 0 < url.length()) {
                            switch (nParam) {
                                case 1:
                                    gsPhotoReference1 = url;
                                    break;
                                case 2:
                                    gsPhotoReference2 = url;
                                    break;
                                case 3:
                                    gsPhotoReference3 = url;
                                    break;
                                case 4:
                                    gsPhotoReference4 = url;
                                    break;
                                case 5:
                                    gsPhotoReference5 = url;
                                    break;
                                case 6:
                                    gsPhotoReference6 = url;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, NewInjuryActivity.this);
                LatLng latLng = place.getLatLng();
                final String toastMsg = String.format("Place: %s", place.getName());
                String link_val = "www.google.com/maps?q="+ latLng.latitude + "," + latLng.longitude;
                ShortURL.makeShortUrl(link_val, new ShortURL.ShortUrlListener() {
                            @Override
                            public void OnFinish(String url) {
                                if (url != null && 0 < url.length()) {
                                    spannedLocation = Html.fromHtml("<a href=\"" + url + "\">" + url + "</a>");
                                    etLocation.setText(spannedLocation);
                                    etLocation.setMovementMethod(LinkMovementMethod.getInstance());
                                    Toast.makeText(NewInjuryActivity.this, toastMsg, Toast.LENGTH_LONG).show();                                }
                            }
                        });
            }
        } else if (resultCode == RESULT_OK ) {
            Uri photoUri = data.getData();
            uploadImage(photoUri, requestCode);
            switch (requestCode) {
                case 1:
                    photoUri1 = photoUri;
                    imgPhoto1.setImageURI(null);
                    imgPhoto1.setImageURI(photoUri);
                    break;
                case 2:
                    photoUri2 = photoUri;
                    imgPhoto2.setImageURI(null);
                    imgPhoto2.setImageURI(photoUri);
                    break;
                case 3:
                    photoUri3 = photoUri;
                    imgPhoto3.setImageURI(null);
                    imgPhoto3.setImageURI(photoUri);
                    break;
                case 4:
                    photoUri4 = photoUri;
                    imgPhoto4.setImageURI(null);
                    imgPhoto4.setImageURI(photoUri);
                    break;
                case 5:
                    photoUri5 = photoUri;
                    imgPhoto5.setImageURI(null);
                    imgPhoto5.setImageURI(photoUri);
                    break;
                case 6:
                    photoUri6 = photoUri;
                    imgPhoto6.setImageURI(null);
                    imgPhoto6.setImageURI(photoUri);
                    break;
                default:
                    break;
            }
            //imgPhoto1.setImageBitmap(bitmap);
        }
    }

    private void locationPick() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(NewInjuryActivity.this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {

        }
    }

    private void onSubmit() {
        if(etLocation.getText().toString().isEmpty()) {
            showAlert("Location* required.", null);
            return;
        }
        if(etDate.getText().toString().isEmpty()) {
            showAlert("Date* required.", null);
            return;
        }
        if(etTime.getText().toString().isEmpty()) {
            showAlert("Time* required.", null);
            return;
        }
        if(etYourName.getText().toString().isEmpty()) {
            showAlert("Your Name* required.", etYourName);
            return;
        }
        if(etYourAddress.getText().toString().isEmpty()) {
            showAlert("Your Address* required.", etYourAddress);
            return;
        }
        if(etYourPhone.getText().toString().isEmpty()) {
            showAlert("Your Phone Number* required.", etYourPhone);
            return;
        }
        if(etYourLicense.getText().toString().isEmpty()) {
            showAlert("Your License Plate* required.", etYourLicense);
            return;
        }
        if(photoUri1 == null) {
            showAlert("1st Accident Photo* required.", null);
            return;
        }
        if(photoUri2 == null) {
            showAlert("2nd Accident Photo* required.", null);
            return;
        }
        if(photoUri3 == null) {
            showAlert("3rd Accident Photo* required.", null);
            return;
        }

        final CircularProgressView progressView = (CircularProgressView) findViewById(R.id.progress_view);
        progressView.startAnimation();
        progressView.setVisibility(View.VISIBLE);
        //Check Photo Upload
        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                int nPhotoCnt = 0;
                if(photoUri1 != null)
                    nPhotoCnt ++;
                if(photoUri2 != null)
                    nPhotoCnt ++;
                if(photoUri3 != null)
                    nPhotoCnt ++;
                if(photoUri4 != null)
                    nPhotoCnt ++;
                if(photoUri5 != null)
                    nPhotoCnt ++;
                if(photoUri6 != null)
                    nPhotoCnt ++;
                int nUploadedCnt = 0;
                if(gsPhotoReference1!=null)
                    nUploadedCnt ++;
                if(gsPhotoReference2!=null)
                    nUploadedCnt ++;
                if(gsPhotoReference3!=null)
                    nUploadedCnt ++;
                if(gsPhotoReference4!=null)
                    nUploadedCnt ++;
                if(gsPhotoReference5!=null)
                    nUploadedCnt ++;
                if(gsPhotoReference6!=null)
                    nUploadedCnt ++;

                if(nUploadedCnt == nPhotoCnt) {
                    h.removeCallbacks(this);
                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                    submitForm();
                } else {
                    h.postDelayed(this, 100);
                }
            }
        }, 1000); // 1 second delay (takes millis)

    }

    private void submitForm() {

        String strReport = "<b>Location*:</b> " + spannedLocation +"<br>";
        strReport += "<b>Date*:</b> " + etDate.getText().toString() + "<br>";
        strReport += "<b>Time*:</b> " + etTime.getText().toString() + "<br>";

        strReport += "<br><b>Your Name*:</b> " + etYourName.getText().toString() + "<br>";
        strReport += "<b>Your Phone*:</b> " + etYourPhone.getText().toString() + "<br>";
        strReport += "<b>Your Address*:</b> " + etYourAddress.getText().toString() + "<br>";
        strReport += "<b>Your License Plate*:</b> " + etYourLicense.getText().toString() + "<br><br>";

        strReport += "<b>1st Accident Photo*:</b> <a href=\"" + gsPhotoReference1 + "\">" + gsPhotoReference1 + "</a><br>";
        strReport += "<b>2nd Accident Photo*:</b> <a href=\"" + gsPhotoReference2 + "\">" + gsPhotoReference2 + "</a><br>";
        strReport += "<b>3rd Accident Photo*:</b> <a href=\"" + gsPhotoReference3 + "\">" + gsPhotoReference3 + "</a><br>";

        strReport += "<br><b>-Other Driver's Information</b><br><br>";
        strReport += "<b>Other Driver's Name:</b> " + etDriverName.getText().toString() + "<br>";
        strReport += "<b>Other Driver's Phone Number:</b> " + etDriverPhone.getText().toString() + "<br>";
        strReport += "<b>Other Driver's License Plate:</b> " + etDriverLicense.getText().toString() + "<br>";
        strReport += "<b>Vehicle Make/Model:</b> " + etVehicle.getText().toString() + "<br>";
        strReport += "<b>Insurance Company:</b> " + etInsuranceCompany.getText().toString() + "<br><br>";

        strReport += "<b>-Witness Information</b><br><br>";
        strReport += "<b>Witness 1 Name:</b> " + etWitnessName1.getText().toString() + "<br>";
        strReport += "<b>Witness 1 Phone Number:</b> " + etWitnessPhone1.getText().toString() + "<br>";
        strReport += "<b>Witness 2 Name:</b><br>" + etWitnessName2.getText().toString() + "<br>";
        strReport += "<b>Witness 2 Phone Number:</b> " + etWitnessPhone2.getText().toString() + "<br><br>";

        strReport += "<b>-Injured Information</b><br><br>";
        strReport += "<b>Injured 1 Name:</b> " + etInjuredName1.getText().toString() + "<br>";
        strReport += "<b>Injured 1 Phone Number:</b> " + etInjuredPhone1.getText().toString() + "<br>";
        strReport += "<b>Injured 1 Photo:</b><br>";
        if(gsPhotoReference4!=null) {
            strReport += "<a href=\"" + gsPhotoReference4 + "\">" + gsPhotoReference4 + "</a><br>";
        }

        strReport += "<br><b>Injured 2 Name:</b> " + etInjuredName2.getText().toString() + "<br>";
        strReport += "<b>Injured 2 Phone Number:</b> " + etInjuredPhone2.getText().toString() + "<br>";
        strReport += "<b>Injured 2 Photo:</b> ";
        if(gsPhotoReference5!=null) {
            strReport += "<a href=\"" + gsPhotoReference5 + "\">" + gsPhotoReference5 + "</a><br>";
        }

        strReport += "<br><b>-Police Information</b><br><br>";
        strReport += "<b>Police Name:</b> " + etPoliceName.getText().toString() + "<br>";
        strReport += "<b>Police Number:</b " + etPoliceNumber.getText().toString() + "<br>";
        strReport += "<b>Police Report Number:</b> " + etPoliceReportNumber.getText().toString() + "<br>";
        strReport += "<b>Police Photo:</b> ";
        if(gsPhotoReference6!=null) {
            strReport += "<a href=\"" + gsPhotoReference6 + "\">" + gsPhotoReference6 + "</a><br>";
        }


        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"phil@hlaw.org", "lisa@hlaw.org", "kim@hlaw.org", "jeff@hlaw.org"});
        i.putExtra(Intent.EXTRA_SUBJECT, "New Injury Report");
        i.putExtra(Intent.EXTRA_TEXT   , Html.fromHtml(strReport));
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NewInjuryActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlert(String text, final EditText editText) {
        new AlertDialog.Builder(this)
                .setTitle("Input Error")
                .setMessage(text)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText != null)
                        {
                            editText.requestFocus();
                        }
                    }
                })

                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
        return;
    }
}
