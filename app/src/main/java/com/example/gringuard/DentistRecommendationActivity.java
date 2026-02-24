package com.example.gringuard;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DentistRecommendationActivity extends AppCompatActivity {

    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentist_recommendation);

        container = findViewById(R.id.containerDentists);

        addState("Andhra Pradesh");
        addDentist(
                "Dr. Suresh Reddy",
                "Clove Dental Clinic",
                "040-33553245",
                "Dwaraka Nagar, Visakhapatnam, Andhra Pradesh 530016"
        );

        addState("Arunachal Pradesh");
        addDentist(
                "Dr. T. Wangmu",
                "Happy Teeth Dental Clinic",
                "07005726334",
                "Bank Tinali, Naharlagun, Arunachal Pradesh 791110"
        );

        addState("Assam");
        addDentist(
                "Dr. Anupam Das",
                "Smile Care Dental Clinic",
                "07086555888",
                "GS Road, Christian Basti, Guwahati, Assam 781005"
        );

        addState("Bihar");
        addDentist(
                "Dr. R. K. Sinha",
                "Sinha Dental Care",
                "06122219820",
                "Boring Road, Patna, Bihar 800001"
        );

        addState("Chhattisgarh");
        addDentist(
                "Dr. Neeraj Agrawal",
                "Dental World",
                "07714060707",
                "Shankar Nagar, Raipur, Chhattisgarh 492007"
        );

        addState("Goa");
        addDentist(
                "Dr. Anthony Menezes",
                "Menezes Dental Clinic",
                "08322425444",
                "18th June Road, Panaji, Goa 403001"
        );

        addState("Gujarat");
        addDentist(
                "Dr. Nisha Shah",
                "Clove Dental Clinic",
                "07940193666",
                "Satellite Road, Ahmedabad, Gujarat 380015"
        );

        addState("Haryana");
        addDentist(
                "Dr. Amit Khatri",
                "32 Pearls Dental Clinic",
                "01244383030",
                "DLF Phase 4, Gurugram, Haryana 122002"
        );

        addState("Himachal Pradesh");
        addDentist(
                "Dr. Sunita Thakur",
                "Smile Dental Clinic",
                "09816045010",
                "Mall Road, Shimla, Himachal Pradesh 171001"
        );

        addState("Jharkhand");
        addDentist(
                "Dr. Prashant Verma",
                "Ranchi Dental Care",
                "06512330444",
                "Lalpur, Ranchi, Jharkhand 834001"
        );

        addState("Karnataka");
        addDentist(
                "Dr. Ananya Rao",
                "Clove Dental Clinic",
                "08047187000",
                "Indiranagar, Bengaluru, Karnataka 560038"
        );

        addState("Kerala");
        addDentist(
                "Dr. S. Anil Kumar",
                "Ananthapuri Dental Clinic",
                "04712550006",
                "Pattom, Thiruvananthapuram, Kerala 695004"
        );

        addState("Madhya Pradesh");
        addDentist(
                "Dr. Manish Jain",
                "Perfect 32 Dental Clinic",
                "07314200400",
                "Vijay Nagar, Indore, Madhya Pradesh 452010"
        );

        addState("Maharashtra");
        addDentist(
                "Dr. Abhishek Soni",
                "Clove Dental Clinic",
                "02240193666",
                "Andheri West, Mumbai, Maharashtra 400058"
        );

        addState("Manipur");
        addDentist(
                "Dr. L. Romesh Singh",
                "City Dental Clinic",
                "07005333445",
                "Paona Bazaar, Imphal, Manipur 795001"
        );

        addState("Meghalaya");
        addDentist(
                "Dr. R. Lyngdoh",
                "Bright Smile Dental Clinic",
                "09862677477",
                "Laitumkhrah, Shillong, Meghalaya 793003"
        );

        addState("Mizoram");
        addDentist(
                "Dr. Lalnunmawia",
                "Grace Dental Clinic",
                "09863055524",
                "Chandmary, Aizawl, Mizoram 796007"
        );

        addState("Nagaland");
        addDentist(
                "Dr. Temjen Jamir",
                "Kohima Dental Care",
                "09612388022",
                "PR Hill, Kohima, Nagaland 797001"
        );

        addState("Odisha");
        addDentist(
                "Dr. Sandeep Mishra",
                "Clove Dental Clinic",
                "06743500300",
                "Jaydev Vihar, Bhubaneswar, Odisha 751013"
        );

        addState("Punjab");
        addDentist(
                "Dr. Harpreet Kaur",
                "Perfect Smile Dental Clinic",
                "01835090909",
                "Mall Road, Amritsar, Punjab 143001"
        );

        addState("Rajasthan");
        addDentist(
                "Dr. Rakesh Sharma",
                "Jaipur Dental Hospital",
                "01414040470",
                "C-Scheme, Jaipur, Rajasthan 302001"
        );

        addState("Sikkim");
        addDentist(
                "Dr. Tenzing Bhutia",
                "Gangtok Dental Care",
                "03592202728",
                "Deorali, Gangtok, Sikkim 737102"
        );

        addState("Tamil Nadu");
        addDentist(
                "Dr. R. Venkatesh",
                "Apollo White Dental",
                "04442999999",
                "Anna Nagar, Chennai, Tamil Nadu 600040"
        );

        addState("Telangana");
        addDentist(
                "Dr. Kiran Kumar",
                "FMS Dental Hospital",
                "04045678999",
                "Punjagutta, Hyderabad, Telangana 500082"
        );

        addState("Tripura");
        addDentist(
                "Dr. Arup Deb",
                "Smile Care Dental Clinic",
                "03812313131",
                "Krishna Nagar, Agartala, Tripura 799001"
        );

        addState("Uttar Pradesh");
        addDentist(
                "Dr. Ankit Verma",
                "Clove Dental Clinic",
                "05224100400",
                "Gomti Nagar, Lucknow, Uttar Pradesh 226010"
        );

        addState("Uttarakhand");
        addDentist(
                "Dr. Pankaj Gupta",
                "Dehradun Dental Clinic",
                "01352768000",
                "Rajpur Road, Dehradun, Uttarakhand 248001"
        );

        addState("West Bengal");
        addDentist(
                "Dr. Arindam Bose",
                "The Tooth Doctors",
                "03340602020",
                "Salt Lake, Kolkata, West Bengal 700091"
        );
    }

    private void addState(String stateName) {
        TextView tv = new TextView(this);
        tv.setText("\n" + stateName);
        tv.setTextSize(18);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        tv.setTextColor(0xFFAD1457);
        container.addView(tv);
    }

    private void addDentist(String dentist, String clinic, String phone, String address) {
        TextView tv = new TextView(this);
        tv.setText(
                "\n🦷 Dentist: " + dentist +
                        "\n🏥 Clinic: " + clinic +
                        "\n📞 Phone: " + phone +
                        "\n📍 Address: " + address + "\n"
        );
        tv.setTextSize(15);
        tv.setTextColor(0xFF880E4F);
        tv.setPadding(14, 14, 14, 14);
        tv.setBackgroundColor(0xFFFFF0F6);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 8);
        tv.setLayoutParams(params);

        container.addView(tv);
    }
}
