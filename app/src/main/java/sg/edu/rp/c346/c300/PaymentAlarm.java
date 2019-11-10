package sg.edu.rp.c346.c300;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.PrePayment;

public class PaymentAlarm extends BroadcastReceiver {

    ArrayList<PrePayment> prePaymentCompleteList = new ArrayList<>();
    ArrayList<PrePayment> notCompletePrePaymentList = new ArrayList<>();

    int prepaymentCompleteIndexOfPrePaymentCompleteList;


    ArrayList<Collection> tcCollection = new ArrayList<>();
    int numOfOrderIndex;

    int stallIndex;

    String purchasedDateTime = "";

    double amountCustomer=0;

    String date =(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy"));
    String dateTimePurchased =MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a");

    @Override
    public void onReceive(final Context context, Intent intent) {


        purchasedDateTime = MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a");

        final DatabaseReference drPrePayment = FirebaseDatabase.getInstance().getReference().child("prePayment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drPrePayment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfPrePayment = Integer.parseInt(dataSnapshot.child("numOfPrePayment").getValue().toString());


                for (int i =0; i<numOfPrePayment;i++){

                    Date paymentDateTime = MainpageActivity.convertStringToDate(dataSnapshot.child(i+"").child("paymentDateTime").getValue().toString(), "dd/MM/yyyy h:mm a");
                    Date currentDateTime = Calendar.getInstance().getTime();

                    if (paymentDateTime.compareTo(currentDateTime) <0){
                        prePaymentCompleteList.add(dataSnapshot.child(i+"").getValue(PrePayment.class));
                    }
                    else{
                        notCompletePrePaymentList.add(dataSnapshot.child(i+"").getValue(PrePayment.class));

                    }
                    drPrePayment.child(i+"").removeValue();

                }



                final DatabaseReference drPostPayment = FirebaseDatabase.getInstance().getReference().child("postPayment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                drPostPayment.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int numOfPostPayment = Integer.parseInt(dataSnapshot.child("numOfPostPayment").getValue().toString());

                        Log.d("prePayment Size", "((((((()()()()()())()()())()())()()()())What is the size: "+prePaymentCompleteList.size());
                        Log.d("postPayment Size", "((((((()()()()()())()()())()())()()()())What is the size: "+notCompletePrePaymentList.size());

                        for (int i =0; i<prePaymentCompleteList.size();i++){
                            drPostPayment.child((numOfPostPayment+i)+"").setValue(prePaymentCompleteList.get(i));
                            drPostPayment.child((numOfPostPayment+i)+"").child("paymentMadeDateTime").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));
                        }

                        drPostPayment.child("numOfPostPayment").setValue(numOfPostPayment+prePaymentCompleteList.size());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //region Add back those that are not over the current timing
                for (int num =0; num<notCompletePrePaymentList.size();num++){
                    drPrePayment.child(num+"").setValue(notCompletePrePaymentList.get(num));
                }

                drPrePayment.child("numOfPrePayment").setValue(notCompletePrePaymentList.size());

                //endregion





//                for (prepaymentCompleteIndexOfPrePaymentCompleteList =0; prepaymentCompleteIndexOfPrePaymentCompleteList<prePaymentCompleteList.size(); prepaymentCompleteIndexOfPrePaymentCompleteList++){


                    //region Find the position in TC
                    final DatabaseReference drTC = FirebaseDatabase.getInstance().getReference().child("tc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order");
                    drTC.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                            int numOfOrder = Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString());
                            int resultOfOrder = numOfOrder; //final result of the numOfOrder for TC

                            Log.d("54545", "What is the number of order: "+numOfOrder);

                            for (numOfOrderIndex =0; numOfOrderIndex<numOfOrder; numOfOrderIndex++){


                                        String name = dataSnapshot.child(numOfOrderIndex+"").child("name").getValue().toString();
                                        double price = Double.parseDouble(dataSnapshot.child(numOfOrderIndex+"").child("price").getValue().toString());
                                        String dataTimeOrder = dataSnapshot.child(numOfOrderIndex+"").child("dateTimeOrder").getValue().toString();
                                        int quantity = Integer.parseInt(dataSnapshot.child(numOfOrderIndex+"").child("quantity").getValue().toString());
                                        String stallName = dataSnapshot.child(numOfOrderIndex+"").child("stallName").getValue().toString();
                                        int stallId = Integer.parseInt(dataSnapshot.child(numOfOrderIndex+"").child("stallId").getValue().toString());
                                        int foodId = Integer.parseInt(dataSnapshot.child(numOfOrderIndex+"").child("foodId").getValue().toString());
                                        double totalPrice = Double.parseDouble(dataSnapshot.child(numOfOrderIndex+"").child("totalPrice").getValue().toString());
                                        String additionalNote = dataSnapshot.child(numOfOrderIndex+"").child("additionalNote").getValue().toString();
                                        String lastChanges = dataSnapshot.child(numOfOrderIndex+"").child("lastChanges").getValue().toString();
                                        int lastChangesInMin = Integer.parseInt(dataSnapshot.child(numOfOrderIndex+"").child("lastChangesInMin").getValue().toString());
                                        String tId = dataSnapshot.child(numOfOrderIndex+"").child("tId").getValue().toString();
                                        String startTime = dataSnapshot.child(numOfOrderIndex+"").child("startTime").getValue().toString();
                                        String endTime = dataSnapshot.child(numOfOrderIndex+"").child("endTime").getValue().toString();
                                        String customerUID = dataSnapshot.child(numOfOrderIndex+"").child("customerUID").getValue().toString();
                                        String stallUID = dataSnapshot.child(numOfOrderIndex+"").child("stallUID").getValue().toString();
                                        String status = dataSnapshot.child(numOfOrderIndex+"").child("status").getValue().toString();
                                        String image = dataSnapshot.child(numOfOrderIndex+"").child("imageurl").getValue().toString();
                                        String school = dataSnapshot.child(numOfOrderIndex+"").child("school").getValue().toString();
                                        int numOfAddOn = Integer.parseInt(dataSnapshot.child(numOfOrderIndex+"").child("addOn").child("numOfAddOn").getValue().toString());

                                        ArrayList<AddOn> addOnList = new ArrayList<>();
                                        for (int h =0; h < numOfAddOn;h++){
                                            String addOnName = dataSnapshot.child(numOfOrderIndex+"").child("addOn").child(h+"").child("name").getValue().toString();
                                            double addOnPrice = Double.parseDouble(dataSnapshot.child(numOfOrderIndex+"").child("addOn").child(h+"").child("price").getValue().toString());
                                            addOnList.add(new AddOn(addOnName, addOnPrice));
                                        }

                                        final Collection collection = new Collection(name, price, dataTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, additionalNote, lastChanges, lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status,image, school);

                                        tcCollection.add(collection);

                                        drTC.child(numOfOrderIndex+"").removeValue();
                                        --resultOfOrder;
                                        drTC.child("numOfOrder").setValue(resultOfOrder);

//                                        final DatabaseReference drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
//                                        drHC.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString());
//                                                drHC.child(numOfPreOrder+"").setValue(collection);
//                                                drHC.child("numOfPreOrder").setValue(numOfPreOrder+1);
//                                                Log.d("787878", " I am HERE"+ numOfPreOrder);
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });





                                        final DatabaseReference drTO = FirebaseDatabase.getInstance().getReference().child("to").child("school").child(collection.getSchool()).child("stall").child(stallId+"").child("order");
                                        drTO.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                int numOfOrder = Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString());
//
//                                                int resultNumOfOrderTO =numOfOrder;
//
//                                                int position =0;
//
//                                                Log.d("787878", "What is numOofOrder: "+numOfOrder);
//
//                                                for (int i =0; i<numOfOrder; i++){
//                                                    Log.d("787878","What is the tID: "+collection.gettId());
//                                                    if (dataSnapshot.child(i+"").child("tId").getValue().toString().equals(collection.gettId())){
//                                                        position = i;
//
//                                                        Log.d("787878","What tid is correct: "+collection.gettId());
//
//                                                        for (int num = position; i<numOfOrder-1; i++){
//                                                            String name = dataSnapshot.child((num+1)+"").child("name").getValue().toString();
//                                                            double price = Double.parseDouble(dataSnapshot.child((num+1)+"").child("price").getValue().toString());
//                                                            String dataTimeOrder = dataSnapshot.child((num+1)+"").child("dateTimeOrder").getValue().toString();
//                                                            int quantity = Integer.parseInt(dataSnapshot.child((num+1)+"").child("quantity").getValue().toString());
//                                                            String stallName = dataSnapshot.child((num+1)+"").child("stallName").getValue().toString();
//                                                            int stallId = Integer.parseInt(dataSnapshot.child((num+1)+"").child("stallId").getValue().toString());
//                                                            int foodId = Integer.parseInt(dataSnapshot.child((num+1)+"").child("foodId").getValue().toString());
//                                                            double totalPrice = Double.parseDouble(dataSnapshot.child((num+1)+"").child("totalPrice").getValue().toString());
//                                                            String additionalNote = dataSnapshot.child((num+1)+"").child("additionalNote").getValue().toString();
//                                                            String lastChanges = dataSnapshot.child((num+1)+"").child("lastChanges").getValue().toString();
//                                                            int lastChangesInMin = Integer.parseInt(dataSnapshot.child((num+1)+"").child("lastChangesInMin").getValue().toString());
//                                                            String tId = dataSnapshot.child((num+1)+"").child("tId").getValue().toString();
//                                                            String startTime = dataSnapshot.child((num+1)+"").child("startTime").getValue().toString();
//                                                            String endTime = dataSnapshot.child((num+1)+"").child("endTime").getValue().toString();
//                                                            String customerUID = dataSnapshot.child((num+1)+"").child("customerUID").getValue().toString();
//                                                            String stallUID = dataSnapshot.child((num+1)+"").child("stallUID").getValue().toString();
//                                                            String status = dataSnapshot.child((num+1)+"").child("status").getValue().toString();
//                                                            String image = dataSnapshot.child((num+1)+"").child("imageurl").getValue().toString();
//                                                            String school = dataSnapshot.child((num+1)+"").child("school").getValue().toString();
//                                                            int numOfAddOn = Integer.parseInt(dataSnapshot.child((num+1)+"").child("addOn").child("numOfAddOn").getValue().toString());
//
//                                                            ArrayList<AddOn> addOnList = new ArrayList<>();
//                                                            for (int h =0; h < numOfAddOn;h++){
//                                                                String addOnName = dataSnapshot.child((num+1)+"").child("addOn").child(h+"").child("name").getValue().toString();
//                                                                double addOnPrice = Double.parseDouble(dataSnapshot.child((num+1)+"").child("addOn").child(h+"").child("price").getValue().toString());
//                                                                addOnList.add(new AddOn(addOnName, addOnPrice));
//                                                            }
//
//                                                            Collection collection = new Collection(name, price, dataTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, additionalNote, lastChanges, lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status,image, school);
//
//                                                            drTO.child(i+"").setValue(collection);
//                                                        }
//                                                        --resultNumOfOrderTO;
//                                                        drTO.child((resultNumOfOrderTO)+"").removeValue();
//
//                                                    }
//
//
//                                                }
//
//
//                                                drTO.child("numOfOrder").setValue(resultNumOfOrderTO);

                                                //-----------------------------------------------------------------------------------------------------------

//                                                final DatabaseReference drHO = FirebaseDatabase.getInstance().getReference().child("ho").child("school").child(collection.getSchool()).child("stall").child(collection.getStallId()+"").child("preOrder");
//                                                drHO.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                        int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString());
//
//                                                        drHO.child(numOfPreOrder+"").setValue(collection);
//
//                                                        drHO.child("numOfPreOrder").setValue(numOfPreOrder+1);
//
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                    }
//                                                });

                                                int numOfOrderTO = Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString());

                                                ArrayList<Collection> notRelatedTOList = new ArrayList();

                                                for (int i =0; i<numOfOrderTO; i++){
                                                    String name = dataSnapshot.child(i+"").child("name").getValue().toString();
                                                    double price = Double.parseDouble(dataSnapshot.child(i+"").child("price").getValue().toString());
                                                    String dataTimeOrder = dataSnapshot.child(i+"").child("dateTimeOrder").getValue().toString();
                                                    int quantity = Integer.parseInt(dataSnapshot.child(i+"").child("quantity").getValue().toString());
                                                    String stallName = dataSnapshot.child(i+"").child("stallName").getValue().toString();
                                                    int stallId = Integer.parseInt(dataSnapshot.child(i+"").child("stallId").getValue().toString());
                                                    int foodId = Integer.parseInt(dataSnapshot.child(i+"").child("foodId").getValue().toString());
                                                    double totalPrice = Double.parseDouble(dataSnapshot.child(i+"").child("totalPrice").getValue().toString());
                                                    String additionalNote = dataSnapshot.child(i+"").child("additionalNote").getValue().toString();
                                                    String lastChanges = dataSnapshot.child(i+"").child("lastChanges").getValue().toString();
                                                    int lastChangesInMin = Integer.parseInt(dataSnapshot.child(i+"").child("lastChangesInMin").getValue().toString());
                                                    String tId = dataSnapshot.child(i+"").child("tId").getValue().toString();
                                                    String startTime = dataSnapshot.child(i+"").child("startTime").getValue().toString();
                                                    String endTime = dataSnapshot.child(i+"").child("endTime").getValue().toString();
                                                    String customerUID = dataSnapshot.child(i+"").child("customerUID").getValue().toString();
                                                    String stallUID = dataSnapshot.child(i+"").child("stallUID").getValue().toString();
                                                    String status = dataSnapshot.child(i+"").child("status").getValue().toString();
                                                    String image = dataSnapshot.child(i+"").child("imageurl").getValue().toString();
                                                    String school = dataSnapshot.child(i+"").child("school").getValue().toString();
                                                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(i+"").child("addOn").child("numOfAddOn").getValue().toString());

                                                    ArrayList<AddOn> addOnList = new ArrayList<>();
                                                    for (int h =0; h < numOfAddOn;h++){
                                                        String addOnName = dataSnapshot.child(i+"").child("addOn").child(h+"").child("name").getValue().toString();
                                                        double addOnPrice = Double.parseDouble(dataSnapshot.child(i+"").child("addOn").child(h+"").child("price").getValue().toString());
                                                        addOnList.add(new AddOn(addOnName, addOnPrice));
                                                    }

                                                    Collection collection = new Collection(name, price, dataTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, additionalNote, lastChanges, lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status,image, school);

                                                    notRelatedTOList.add(collection);

                                                    drTO.child(i+"").removeValue();
                                                }


                                                for (int i =0; i<tcCollection.size(); i++){
                                                    for (int h=0; h<notRelatedTOList.size();h++){
                                                        if (tcCollection.get(i).gettId().equals(notRelatedTOList.get(h).gettId())){
                                                            notRelatedTOList.remove(h);
                                                            break;
                                                        }
                                                    }
                                                }


                                                for (int i =0; i<notRelatedTOList.size();i++){
                                                    drTO.child(i+"").setValue(notRelatedTOList.get(i));
                                                    drTO.child(i+"").child("addOn").child("numOfAddOn").setValue(notRelatedTOList.get(i).getAddOn().size());
                                                }

                                                drTO.child("numOfOrder").setValue(notRelatedTOList.size());




                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });















                                    }




                                    Log.d("All Size", "What is the size of tcCollection: "+tcCollection.size());


                            final DatabaseReference drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
                            drHC.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int numOfPreOrderHC = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString());

                                    for (int i =0; i<tcCollection.size();i++){
                                        drHC.child((numOfPreOrderHC+i)+"").setValue(tcCollection.get(i));
                                        drHC.child((numOfPreOrderHC+i)+"").child("dateTimePurchased").setValue(dateTimePurchased);
                                        drHC.child((numOfPreOrderHC+i)+"").child("addOn").child("numOfAddOn").setValue(tcCollection.get(i).getAddOn().size());
                                    }

                                    drHC.child("numOfPreOrder").setValue(numOfPreOrderHC+tcCollection.size());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            final Map<String, Integer> indexList = new Hashtable<>(); //split out based on individual stall
                            final Map<String, Double> totalAmountList = new Hashtable<>(); //total amount for each owner
                            final ArrayList<String> locationList = new ArrayList<>();
                            final ArrayList<Integer> stallIDList = new ArrayList<>();
                            final ArrayList<String> stallUIDList = new ArrayList<>();


                            final DatabaseReference drHO = FirebaseDatabase.getInstance().getReference().child("ho").child("school");
                            drHO.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {




                                    for (int i =0; i<tcCollection.size();i++){
                                        int numOfPreOrderHO = Integer.parseInt(dataSnapshot.child(tcCollection.get(i).getSchool()).child("stall").child(tcCollection.get(i).getStallId()+"").child("preOrder").child("numOfPreOrder").getValue().toString());

                                        Log.d("989898", "what is the numOfPreOrderHO: "+numOfPreOrderHO);

                                        int extra =0;
                                        if (indexList.containsKey(tcCollection.get(i).getSchool()+tcCollection.get(i).getStallId())){
                                            extra = indexList.get(tcCollection.get(i).getSchool()+tcCollection.get(i).getStallId());
                                            indexList.put(tcCollection.get(i).getSchool()+tcCollection.get(i).getStallId(), indexList.get(tcCollection.get(i).getSchool()+tcCollection.get(i).getStallId())+1);
                                            totalAmountList.put(tcCollection.get(i).getSchool()+tcCollection.get(i).getStallId(),indexList.get(tcCollection.get(i).getSchool()+tcCollection.get(i).getStallId())+tcCollection.get(i).getTotalPrice());

                                        }
                                        else{
                                            indexList.put(tcCollection.get(i).getSchool()+tcCollection.get(i).getStallId(), 1);
                                            totalAmountList.put(tcCollection.get(i).getSchool()+tcCollection.get(i).getStallId(),tcCollection.get(i).getTotalPrice());

                                            locationList.add(tcCollection.get(i).getSchool());
                                            stallIDList.add(tcCollection.get(i).getStallId());
                                            stallUIDList.add(tcCollection.get(i).getStallUID());
                                        }

                                        amountCustomer += tcCollection.get(i).getTotalPrice();

                                        drHO.child(tcCollection.get(i).getSchool()).child("stall").child(tcCollection.get(i).getStallId()+"").child("preOrder").child((numOfPreOrderHO+extra)+"").setValue(tcCollection.get(i));
                                        drHO.child(tcCollection.get(i).getSchool()).child("stall").child(tcCollection.get(i).getStallId()+"").child("preOrder").child((numOfPreOrderHO+extra)+"").child("dateTimePurchased").setValue(dateTimePurchased);
                                        drHO.child(tcCollection.get(i).getSchool()).child("stall").child(tcCollection.get(i).getStallId()+"").child("preOrder").child((numOfPreOrderHO+extra)+"").child("addOn").child("numOfAddOn").setValue(tcCollection.get(i).getAddOn().size());
//                                        drHO.child(tcCollection.get(i).getSchool()).child("stall").child(tcCollection.get(i).getStallId()+"").child("preOrder").child("numOfPreOrder").setValue(numOfPreOrderHO+1);

                                        drHO.child(tcCollection.get(i).getSchool()).child("stall").child(tcCollection.get(i).getStallId()+"").child("preOrder").child("numOfPreOrder").setValue(numOfPreOrderHO+extra+1);

                                    }


                                    for (int i =0; i<locationList.size(); i++){
                                        stallIndex = i;
                                        String identifyAmount = locationList.get(i)+stallIDList.get(i);
                                        updateGraph(date, totalAmountList.get(identifyAmount), stallUIDList.get(i));
                                        Log.d("56565665", "nalsdnkasndlnsaldlkansld,nsadsa");
                                        final DatabaseReference drOwnerBalance = FirebaseDatabase.getInstance().getReference().child("Owner");
                                        drOwnerBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                double ownerBalance = Double.parseDouble(dataSnapshot.child(stallUIDList.get(stallIndex)).child("balance").getValue().toString());
                                                Log.d("owner Balance", "What is the owner Balance: "+ownerBalance);
                                                drOwnerBalance.child(stallUIDList.get(stallIndex)).child("balance").setValue(ownerBalance + totalAmountList.get(locationList.get(stallIndex)+stallIDList.get(stallIndex)));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }

                                    updateCustomerGraph(date, amountCustomer, FirebaseAuth.getInstance().getCurrentUser().getUid(), "spending");
                                    final DatabaseReference drCustomerBalance = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
                                    drCustomerBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            double customerBalance = Double.parseDouble(dataSnapshot.getValue().toString());
                                            drCustomerBalance.setValue(customerBalance+amountCustomer);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });








                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });











                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //endregion



//                }







            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });












        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
    }






    //Update the owner graph
    private void updateGraph(final String date1, final double price1, final String stallUID1){
        //Get the price
        final DatabaseReference dbAccessGraph = FirebaseDatabase.getInstance().getReference().child("graph").child(stallUID1);
        dbAccessGraph.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                int day = Integer.valueOf(date1.substring(0, 2));
                int month = Integer.valueOf(date1.substring(3, 5));
                int year = Integer.valueOf(date1.substring(6, 10));
                String yearString = String.valueOf(year);


                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.MONTH, month - 1);

                System.out.println("Month: " + new SimpleDateFormat("MMMM").format(c.getTime()));
                System.out.println("Day: " + c.get(Calendar.DAY_OF_MONTH));

                int numOfYears = Integer.valueOf(dataSnapshot.child("year").child("numOfYears").getValue().toString());

                //Check HO dates against Graph Dates then setValue of the profits from HO into Graph date's profit

                for (int a = 0; a < numOfYears; a++) {
                    if (yearString.equals(dataSnapshot.child("year").child(String.valueOf(a)).child("year").getValue().toString())) {


                        String fbDate = dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                .child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH) - 1))
                                .child("date").getValue().toString();


                        System.out.println("Input Date: " + date1.substring(0,10));
                        System.out.println("Firebase Date: " + fbDate);
                        if (date1.substring(0,10).equals(fbDate)) {
                            System.out.println("Matched!");
                            double currentProfit = 0;
                            currentProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH) - 1))
                                    .child("profit").getValue().toString());

                            double newProfit = currentProfit + price1;
                            System.out.println("New Profit: " + newProfit);

                            FirebaseDatabase.getInstance().getReference().child("graph").child(stallUID1).child("year")
                                    .child(String.valueOf(a)).child("months").child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH)-1)).child("profit").setValue(newProfit);


                            //Update Month Profit
                            double currentMonthProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child("profit").getValue().toString());

                            double newMonthProfit = currentMonthProfit + price1;

                            FirebaseDatabase.getInstance().getReference().child("graph").child(stallUID1).child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child("profit").setValue(newMonthProfit);

                            //Update Year Profit
                            double currentYearProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child("profit").getValue().toString());

                            double newYearProfit = currentYearProfit + price1;

                            FirebaseDatabase.getInstance().getReference().child("graph").child(stallUID1).child("year").child(String.valueOf(a)).child("profit").setValue(newYearProfit);

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //update the customer spending graph
    private void updateCustomerGraph(final String date1, final double price1, final String customerUID1,final String css){
        final String cssPlusS = css + "s";

//        //Get the price
        final DatabaseReference dbAccessGraph = FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID1).child(css);
        dbAccessGraph.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                int day = Integer.valueOf(date1.substring(0, 2));
                int month = Integer.valueOf(date1.substring(3, 5));
                int year = Integer.valueOf(date1.substring(6, 10));
                String yearString = String.valueOf(year);


                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.MONTH, month - 1);

                System.out.println("Month: " + new SimpleDateFormat("MMMM").format(c.getTime()));
                System.out.println("Day: " + c.get(Calendar.DAY_OF_MONTH));

                int numOfYears = Integer.valueOf(dataSnapshot.child("year").child("numOfYears").getValue().toString());

                //Check HO dates against Graph Dates then setValue of the profits from HO into Graph date's profit

                for (int a = 0; a < numOfYears; a++) {
                    if (yearString.equals(dataSnapshot.child("year").child(String.valueOf(a)).child("year").getValue().toString())) {


                        String fbDate = dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                .child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH) - 1))
                                .child("date").getValue().toString();


                        System.out.println("Input Date: " + date1.substring(0,10));
                        System.out.println("Firebase Date: " + fbDate);
                        if (date1.substring(0,10).equals(fbDate)) {
                            System.out.println("Matched!");
                            double currentProfit = 0;
                            currentProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH) - 1))
                                    .child(cssPlusS).getValue().toString());

                            double newProfit = currentProfit + price1;
                            System.out.println("New Profit: " + newProfit);

                            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID1).child(css).child("year")
                                    .child(String.valueOf(a)).child("months").child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH)-1)).child(cssPlusS).setValue(newProfit);


                            //Update Month Profit
                            double currentMonthProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child(cssPlusS).getValue().toString());

                            double newMonthProfit = currentMonthProfit + price1;

                            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID1).child(css).child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child(cssPlusS).setValue(newMonthProfit);

                            //Update Year Profit
                            double currentYearProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child(cssPlusS).getValue().toString());

                            double newYearProfit = currentYearProfit + price1;

                            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID1).child(css).child("year").child(String.valueOf(a)).child(cssPlusS).setValue(newYearProfit);

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }






}
