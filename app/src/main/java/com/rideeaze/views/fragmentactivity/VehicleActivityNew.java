package com.rideeaze.views.fragmentactivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rideeaze.R;
import com.rideeaze.services.network.model.data.HttpAccepts;
import com.rideeaze.services.network.model.data.HttpLocation;
import com.rideeaze.services.network.model.data.HttpVehicleShape;
import com.rideeaze.services.network.NetworkApi;
import com.rideeaze.services.network.NetworkService;
import com.rideeaze.services.network.model.data.HttpVehicle;
import com.rideeaze.services.network.model.request.HttpDriverTokenRequest;
import com.rideeaze.services.network.model.request.HttpNewCabDescribeRequest;
import com.rideeaze.services.network.model.response.JsonResponse;
import com.rideeaze.views.activity.DriverActivity;
import com.rideeaze.views.dialog.CustomPopup;
import com.util.Const;
import com.util.StorageDataHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by adventis on 2/2/15.
 */
public class VehicleActivityNew extends DriverActivity {

    Button vehicleClass, colorBtn, styleBtn;
    ScrollView update_vehicle_scrollview;
    RelativeLayout style_popup_button_rl, class_popup_button_rl,color_rl;
    LinearLayout main_linear_layout;
    ImageView style_tv, class_tv;
    private Context mContext;

    private PopupWindow classPopup;


    ArrayList<HttpVehicle> sortedVehicleList = new ArrayList<HttpVehicle>();

    private Message msg;
    private List<HttpVehicle> searchCompanyVehicle = new ArrayList<HttpVehicle>();

    Button vehicle_num_list_button;
    Spinner vehicle_num_list;

    final String LOG_TAG = "myLogs";

    int current_class = 0;
    int current_style = 0;
    int current_color = 0;

    String Vclass = "Taxi";
    String Vcolor = "Yellow";
    String Vstyle = "Sedan";

    String Year = "2013";
    String Make = "aaa";
    String Model = "newCar";

    ToggleButton takecc, nosmoking, handicap;

    ImageView mainCarImageView;

    Spinner yearSpinner;

    EditText pickupCharge, perMileCharge, stopeedForTraffic;

    Button maxpassengers_markdown_button;
    Spinner maxpassengers_list;
    String MaxPassenger = "4";

    boolean takeCC = false;
    boolean noSmoking = false;
    boolean handicapAccess = false;

    Button button_cancel, button_update;



    ArrayList<String> num_list = new ArrayList<String>();
    ArrayList<String> vehicleIdList = new ArrayList<String>();
    ArrayList<String> years_list = new ArrayList<String>();

    HttpVehicle currentVehicle;
    List<HttpVehicle> vehicleList = new ArrayList<HttpVehicle>();

    EditText vehicleIdEditText, modelEditText, makeEditText;
    Boolean isCreateNewVehicleMode = false;
    private Menu menu;
    private Boolean isFirstSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vehicle_new);

        mContext = getApplicationContext();

        //forbid showing icon on action bar
        getActionBar().setDisplayShowHomeEnabled(false);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initResourcesMap();
        generateYearsList();
        generateMaxNumOfPassengerList();

        isFirstSelected = getIntent().getBooleanExtra(Const.NO_SELECTED_VEHICLE, false);

        mainCarImageView = (ImageView) findViewById(R.id.vehicle_image_car);

        vehicle_num_list_button = (Button) findViewById(R.id.vehicle_num_list_button);
        vehicle_num_list = (Spinner) findViewById(R.id.vehicle_num_list);
        vehicle_num_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position > vehicleList.size()) {
                    return;
                }

                currentVehicle = vehicleList.get(position);
                updateUIWithNewVehicle();

            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        vehicleIdEditText = (EditText)findViewById(R.id.vehicle_num_edit);

        update_vehicle_scrollview = (ScrollView) findViewById(R.id.update_vehicle_scrollview);
        main_linear_layout = (LinearLayout) findViewById(R.id.main_linear_layout);

        vehicleClass = (Button) findViewById( R.id.class_popup_button);
        class_popup_button_rl = (RelativeLayout) findViewById( R.id.class_popup_button_rl);
        class_tv = (ImageView) findViewById( R.id.class_tv);

        vehicleClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });

        colorBtn = (Button) findViewById( R.id.color_popup_button);
        color_rl = (RelativeLayout) findViewById( R.id.color_rl);
       // setColorBtn();
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initColorPopupMenu();
            }
        });

        styleBtn = (Button) findViewById( R.id.style_popup_button);
        style_popup_button_rl = (RelativeLayout) findViewById( R.id.style_popup_button_rl);
        style_tv = (ImageView) findViewById( R.id.style_tv);

        //setStyleBtn();
        styleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCarPopupMenu();
            }
        });

        // Set list of years in year Spinner
        yearSpinner = (Spinner) findViewById(R.id.year);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_view, years_list);
        yearAdapter.setDropDownViewResource(R.layout.spinner_view);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(yearAdapter.getCount() - 1);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position > years_list.size()) {
                    return;
                }

                if(currentVehicle != null) {
                    currentVehicle.Year = years_list.get(position);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        makeEditText = (EditText) findViewById(R.id.make);
        modelEditText = (EditText) findViewById(R.id.model);


        maxpassengers_markdown_button = (Button) findViewById(R.id.maxpassengers_markdown_button);
        maxpassengers_list = (Spinner) findViewById(R.id.maxpassengers_list);

        ArrayAdapter<String> adapter_max_pes = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.spinner_view, num_list);
        adapter_max_pes.setDropDownViewResource(R.layout.spinner_view);
        maxpassengers_list.setAdapter(adapter_max_pes);

        maxpassengers_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position > num_list.size()) {
                    return;
                }

                if(currentVehicle != null) {
                    currentVehicle.NumberOfPass = num_list.get(position);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        takecc = (ToggleButton) findViewById(R.id.takeCC);
        nosmoking = (ToggleButton) findViewById(R.id.noSmoking);
        handicap = (ToggleButton) findViewById(R.id.handicapAccess);

        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(isCreateNewVehicleMode) {
                    disableNewVehicleMode();
                } else {
                    finish();
                }*/

                finish();
            }
        });

        button_update = (Button) findViewById(R.id.button_update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentVehicle != null) {
                    if (isCreateNewVehicleMode) {
                        currentVehicle.VehicleNumber = vehicleIdEditText.getText().toString();
                    }
                    currentVehicle.Make = makeEditText.getText().toString();
                    currentVehicle.Model = modelEditText.getText().toString();

                    currentVehicle.NoSmoking = nosmoking.isChecked();
                    currentVehicle.HandicapAccess = handicap.isChecked();
                    currentVehicle.TakeCC = takecc.isChecked();

                    makeNewVehicle(currentVehicle);
                }
            }
        });

        SearchCompanyVehicles(StorageDataHelper.getInstance(getApplicationContext()).getAuthToken());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.driver_update_vehicle_new, menu);
        this.menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_vehicle:
                if(isCreateNewVehicleMode) {
                    disableNewVehicleMode();
                } else {
                    enableNewVehicleMode();
                }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }

    private void enableNewVehicleMode() {
        isCreateNewVehicleMode = true;
        vehicle_num_list.setVisibility(View.INVISIBLE);
        vehicle_num_list_button.setVisibility(View.INVISIBLE);
        vehicleIdEditText.setVisibility(View.VISIBLE);

        Bitmap bmpOriginal = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings_screen_plus);
        Bitmap bmResult = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(bmResult);
        tempCanvas.rotate(45, bmpOriginal.getWidth() / 2, bmpOriginal.getHeight() / 2);
        tempCanvas.drawBitmap(bmpOriginal, 0, 0, null);

        menu.getItem(0).setIcon(new BitmapDrawable(getResources(), bmResult));
    }

    private void disableNewVehicleMode() {
        isCreateNewVehicleMode = false;
        vehicle_num_list.setVisibility(View.VISIBLE);
        vehicle_num_list_button.setVisibility(View.VISIBLE);
        vehicleIdEditText.setVisibility(View.INVISIBLE);

        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.settings_screen_plus));

        currentVehicle = vehicleList.get(vehicle_num_list.getSelectedItemPosition());
        updateUIWithNewVehicle();
    }

    private void initResourcesMap() {
        classMap = new HashMap<String, Integer>();
        classMap.put("Car Service",R.drawable.vehicle_class_c_click);
        classMap.put("Taxi",R.drawable.vehicle_class_t_click);
        classMap.put("Shuttle",R.drawable.vehicle_class_s_click );

        colorMap = new HashMap<String, Integer>();
        colorMap.put("Yellow",R.drawable.color_yellow_bg);
        colorMap.put("White",R.drawable.color_white_bg);
        colorMap.put("Black",R.drawable.color_black_bg);
        colorMap.put("Red",R.drawable.color_red_bg);
        colorMap.put("Green",R.drawable.color_green_bg);
        colorMap.put("Blue",R.drawable.color_blue_bg);
        colorMap.put("Orange",R.drawable.color_orange_bg);
        colorMap.put("LightBlue",R.drawable.color_lightblue_bg);
        colorMap.put("Lilac",R.drawable.color_lilac_bg);
        colorMap.put("Crimson",R.drawable.color_crimson_bg);
        colorMap.put("Gray",R.drawable.color_gray_bg);

        styleMap = new HashMap<String, Integer>();
        styleMap.put(HttpVehicle.SEDAN, R.drawable.car_sedan_white_new);
        styleMap.put(HttpVehicle.SUV, R.drawable.car_suv_white_new);
        styleMap.put(HttpVehicle.VAN, R.drawable.car_van_white_new);
        styleMap.put(HttpVehicle.LIMOUSINE, R.drawable.car_limo_white_new);
        styleMap.put(HttpVehicle.MINI_BUS, R.drawable.car_bus_white_new);
        styleMap.put(HttpVehicle.SHUTTLE, R.drawable.car_bus_white_new);
        styleMap.put(HttpVehicle.LIMO_BUS, R.drawable.car_bus_white_new);
        styleMap.put(HttpVehicle.COACH_BUS, R.drawable.car_bus_white_new);
        styleMap.put(HttpVehicle.STRETCHED_SUV, R.drawable.car_bus_white_new);
        styleMap.put(HttpVehicle.PARTY_BUS, R.drawable.car_bus_white_new);
    }

    private void generateYearsList() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for(int i=1960; i < year+1; i++) {
            years_list.add(i+"");
        }
    }

    private void generateMaxNumOfPassengerList() {
        for(int i=1; i < 31; i++) {
            num_list.add(i+"");
        }
    }

    private void setClassBtn() {
        class_tv.setImageResource(0);

        Integer imageResourceId = classMap.get(currentVehicle.Class);
        if(imageResourceId != null) {
            class_tv.setImageResource(imageResourceId);
        }
    }

    private void setStyleBtn() {
        style_tv.setImageResource(0);
        mainCarImageView.setImageResource(0);
        Integer imageResourceId = styleMap.get(currentVehicle.Style);
        if(imageResourceId != null) {
            style_tv.setImageResource(imageResourceId);
            mainCarImageView.setImageResource(imageResourceId);
        }
    }

    private void setColorBtn() {
        colorBtn.setBackgroundResource(0);
        Integer imageResourceId = colorMap.get(currentVehicle.Color);
        if(imageResourceId != null) {
            colorBtn.setBackgroundResource(imageResourceId);
        }
    }

    public void updateVehicleIdSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_view, vehicleIdList);
        adapter.setDropDownViewResource(R.layout.spinner_view);
        vehicle_num_list.setAdapter(adapter);
    }

    public void updateUIWithNewVehicle() {
        if(currentVehicle == null)
            return;

        setClassBtn();
        setStyleBtn();
        setColorBtn();

        ArrayAdapter adapter = (ArrayAdapter) yearSpinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++)
        {
            if(((String)adapter.getItem(position)).contentEquals(currentVehicle.Year))
            {
                yearSpinner.setSelection(position);
                break;
            }
        }

        makeEditText.setText(currentVehicle.Make);
        modelEditText.setText(currentVehicle.Model);

        adapter = (ArrayAdapter) maxpassengers_list.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++)
        {
            if(((String)adapter.getItem(position)).contentEquals(currentVehicle.NumberOfPass))
            {
                maxpassengers_list.setSelection(position);
                break;
            }
        }

        takecc.setChecked(currentVehicle.TakeCC);
        nosmoking.setChecked(currentVehicle.NoSmoking);
        handicap.setChecked(currentVehicle.HandicapAccess);
    }

    public void updateUIWithNewVehicleList() {
        if(vehicleList != null) {
            // Update Spinner with Vehicle List
            vehicleIdList.clear();

            // select curret vehicle
            String taxiID = SplashActivityOld.getTaxiID(VehicleActivityNew.this);
            int positionToSelect = 0;
            for (int i = 0; i < vehicleList.size(); i++) {
                vehicleIdList.add(vehicleList.get(i).VehicleNumber);
                if(vehicleList.get(i).VehicleNumber.equalsIgnoreCase(taxiID)) {
                    positionToSelect = i;
                }
            }
            updateVehicleIdSpinner();

            vehicle_num_list.setSelection(positionToSelect);

            if(isFirstSelected) {
                vehicle_num_list.performClick();
                showSelectedVehicleToolTip();
            }
        }
    }

    private void showSelectedVehicleToolTip() {
        Toast.makeText(this, R.string.please_select_vehicle, Toast.LENGTH_LONG).show();
    }


    public void SearchCompanyVehicles(final String DriverToken) {
        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        HttpDriverTokenRequest httpDriverTokenRequest = new HttpDriverTokenRequest();
        httpDriverTokenRequest.DriverToken = DriverToken;

        NetworkApi api = (new NetworkService()).getApi();
        api.searchCompanyVehicles(httpDriverTokenRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<List<HttpVehicle>>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonResponse<List<HttpVehicle>> listJsonResponse) {
                        if (listJsonResponse.IsSuccess) {
                            searchCompanyVehicle = listJsonResponse.Content;
                            vehicleList = listJsonResponse.Content;
                            updateUIWithNewVehicleList();
                        } else {
                            finish();
                        }
                    }
                });


    }

    public void makeNewVehicle(final HttpVehicle vehicle) {

        if (!isNetworkAvailable()) return;

        DisplayProcessMessage(getResources().getString(R.string.driver_common_please_wait));

        HttpNewCabDescribeRequest newCabRequest = new HttpNewCabDescribeRequest();
        newCabRequest.Token = StorageDataHelper.getInstance(getApplicationContext()).getAuthToken();
        newCabRequest.TaxiID = vehicle.VehicleNumber;

        newCabRequest.VehicleShape = new HttpVehicleShape();
        newCabRequest.VehicleShape.Vclass = vehicle.Class;
        newCabRequest.VehicleShape.Vcolor = vehicle.Color;
        newCabRequest.VehicleShape.Vstyle = vehicle.Style;

        newCabRequest.Year = vehicle.Year;
        newCabRequest.Model = vehicle.Model;
        newCabRequest.Make = vehicle.Make;

        newCabRequest.MaxPassenger = vehicle.NumberOfPass;

        newCabRequest.Accepts = new HttpAccepts();
        newCabRequest.Accepts.TakeCC = vehicle.TakeCC;
        newCabRequest.Accepts.HandicapAccess = vehicle.HandicapAccess;
        newCabRequest.Accepts.NoSmoking = vehicle.NoSmoking;

        newCabRequest.Location = new HttpLocation();
        if (SplashActivityOld.current_location != null) {
            newCabRequest.Location.longitude = SplashActivityOld.current_location.getLongitude();
            newCabRequest.Location.latitude = SplashActivityOld.current_location.getLatitude();
        }

        newCabRequest.DevOS = "Android";
        newCabRequest.PhoneToken = SplashActivityOld.getRegistrationId(VehicleActivityNew.this);


        NetworkApi api = (new NetworkService()).getApi();
        api.newCabDescribe(newCabRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JsonResponse<String>>() {
                    @Override
                    public void onCompleted() {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        DisplayProcessMessage(false);
                    }

                    @Override
                    public void onNext(JsonResponse<String> stringJsonResponse) {
                        String result = stringJsonResponse.Content;
                        if (result
                                .equals("insert or update on table \"herebygps_drivers\" violates foreign key constraint \"herebygps_drivers_companyID_fkey\"\nDETAIL:  Key (companyID)=(0) is not present in table \"herebygps_company\".\n")) {
                            result = "Update Vehlicel Describe Failed!\nReason: Your Merchant Id is registered as 0";
                        }

                        if (result.equals("")) {
                            DisplayStringMessage("Updating Describe failed");
                        } else if (result
                                .equals("insert or update on table \"herebygps_drivers\" violates foreign key constraint \"herebygps_drivers_companyID_fkey\"\nDETAIL:  Key (companyID)=(0) is not present in table \"herebygps_company\".\n")) {
                            DisplayStringMessage("Update Vehicle Describe Failed!\nReason: Your Merchant Id is registered as 0.");
                        } else if (result.contentEquals("Exception")) {
                            DisplayStringMessage(result);
                        } else {
                            DisplayStringMessage(result);

                            SaveVehicleInfo(vehicle);
                            SplashActivityOld.selected_vehicle = true;
                        }
                    }
                });
    }

    public void SaveVehicleInfo(HttpVehicle vehicle) {
        StorageDataHelper.getInstance(this).setSelectedVehicle(vehicle);
    }

    public Map<String, Integer> styleMap;
    public Map<String, Integer> classMap;
    public Map<String, Integer> colorMap;

    private void initiatePopupWindow() {
        CustomPopup popup = new CustomPopup(getApplicationContext());

        popup.setCustomPopupWindowDismiss(new CustomPopup.OnCustomPopupWindowDismiss() {
            @Override
            public void onDismiss() {
                //onDismiss();
                main_linear_layout.setBackgroundColor(Color.TRANSPARENT);
                Log.d("Popup", "Dismiss");
            }
        });
        popup.setCustomPopupWindowSelectitem(new CustomPopup.OnCustomPopupWindowSelectitem() {
            @Override
            public void onSelectItem(int id) {
                Log.d("INDEX", "accept class: "+id+"");
                class_tv.setImageResource(0);
                class_tv.setImageResource(id);

                if(currentVehicle != null) {
                    currentVehicle.Style = getKeyByValue(classMap, id);
                }
            }
        });

        Integer[] classArray = classMap.values().toArray(new Integer[classMap.size()]);
        popup.setData(classArray, class_popup_button_rl);
        onShow();
        popup.show(class_popup_button_rl);
    }

    private String getKeyByValue(Map<String, Integer> map, Integer value) {
        for (Map.Entry<String,Integer> entry : map.entrySet()) {
            if(entry.getValue().intValue() == value.intValue()) {
                return entry.getKey();
            }
        }

        return "";
    }

    private void initColorPopupMenu() {
        CustomPopup popup = new CustomPopup(getApplicationContext());
        popup.setCustomPopupWindowDismiss(new CustomPopup.OnCustomPopupWindowDismiss() {
            @Override
            public void onDismiss() {
                main_linear_layout.setBackgroundColor(Color.TRANSPARENT);
                Log.d("Popup", "Dismisscolor");
            }
        });
        popup.setCustomPopupWindowSelectitem(new CustomPopup.OnCustomPopupWindowSelectitem() {
            @Override
            public void onSelectItem(int id) {
                colorBtn.setBackgroundResource(id);
                if(currentVehicle != null) {
                    currentVehicle.Color = getKeyByValue(colorMap, id);
                }
            }
        });
        Integer[] colorArray = colorMap.values().toArray(new Integer[colorMap.size()]);
        popup.setData(colorArray, colorBtn);
        onShow();
        popup.show(color_rl);
    }

    private void initCarPopupMenu() {
        CustomPopup popup = new CustomPopup(getApplicationContext());
        popup.setCustomPopupWindowDismiss(new CustomPopup.OnCustomPopupWindowDismiss() {
            @Override
            public void onDismiss() {
                main_linear_layout.setBackgroundColor(Color.TRANSPARENT);
                Log.d("Popup", "Dismisscar");
            }
        });
        popup.setCustomPopupWindowSelectitem(new CustomPopup.OnCustomPopupWindowSelectitem() {
            @Override
            public void onSelectItem(int id) {
                Log.d("INDEX", "accept car: "+id+"");
                style_tv.setImageResource(0);
                style_tv.setImageResource(id);
                mainCarImageView.setImageResource(id);

                if(currentVehicle != null) {
                    currentVehicle.Style = getKeyByValue(styleMap, id);
                }
            }
        });
        Integer[] styleArray = styleMap.values().toArray(new Integer[styleMap.size()]);
        popup.setData(styleArray, style_popup_button_rl);
        onShow();
        popup.show(style_popup_button_rl);
    }

    private void onShow(){
    }


}