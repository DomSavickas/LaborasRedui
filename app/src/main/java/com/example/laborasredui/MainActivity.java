package com.example.laborasredui; //Tavo app pavadinimas

import android.app.Activity; //Start of comment: importing libraries. Libraries: sets of functions
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date; //End of comment

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> { //aprasomas musu langas, kuris atsidaris useriui atidarius aplikacija
    private static final String TAG = "CallLog"; //apsirasomas kintamasis String (zodzio) formato TAG ir jam priskiriamas CallLog reiksme
    private static int URL_LOADER = 1; //apsirasomas kintamasis int (skaiciaus) formato pavadinimu URL_LOADER ir jam priskiriama reiksme 1
    private TextView callLogsTextView; //apsirasomas textview elementas

    @Override
    public void onCreate(Bundle savedInstanceState) { //kai ijungiama aplikacija saukiamas onCreate ir jame esancios funkcijos ir t.t. yra perskaitomos
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()"); //loge parasomas pranesimas CallLog, onCreate()

        setContentView(R.layout.main); //priskiriama design failas main musu matomam langui
        initialize(); //iskvieciama initialize funkcija
    }


    private void initialize() { //apsirasoma initialize funkcija
        Log.d(TAG, "initialize()"); //loge parasomas pranesimas CallLog, initialize()
        final Button btnUpdate=(Button) findViewById(R.id.btn_update); // update mygtuko aprasymas
        btnUpdate.setVisibility(View.GONE); //paslepiamas mygtukas
        final Button btnCallLog = (Button) findViewById(R.id.btn_call_log); // show mygtuko aprasymas

        btnCallLog.setOnClickListener(new View.OnClickListener() { //apsirasoma kas bus daroma jei bus paspaustas mygtukas btnCallLog
            @Override
            public void onClick(View v) { //kai paspaudi mygtuka onClick funkcija bus iskvieciama
                try {//bando vygdyti skliausteliuose esanti koda
                    Log.d(TAG, "initialize() >> initialise loader"); //loge parasomas pranesimas CallLog, initialize() >> initialise loader
                    getLoaderManager().initLoader(URL_LOADER, null, MainActivity.this); //iskvieciamas data loaderis siame lange, kurio pav bus URL_LOADER kintamasis
                    btnCallLog.setVisibility(View.GONE); //btnCallLog mygtukas padaromas nematomu
                    btnUpdate.setVisibility(View.VISIBLE); //btnUpdate mygtukas padaromas matomu
                }catch(Exception e){}//jeigu iskylo beda tame kode si eilute pagaus errora ir ji parodis
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //apsirasoma kas bus daroma jei bus paspaustas mygtukas btnUpdate
                try {//bando vygdyti skliausteliuose esanti koda
                    Log.d(TAG, "initialize() >> initialise loader"); //loge parasomas pranesimas CallLog, initialize() >> initialise loader
                    URL_LOADER ++; //prie URL_LOADER reiksmes pridedamas 1, tai jei buvo 1 dabar bus 2
                    getLoaderManager().initLoader(URL_LOADER, null, MainActivity.this);
                }catch(Exception e){}//jeigu iskylo beda tame kode si eilute pagaus errora ir ji parodis
            }
        });

        callLogsTextView = (TextView) findViewById(R.id.call_logs); //surandamas textview elementas pagal jo id ir jam priskiriamas pavadinimas callLogsTextView
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) { //sukuriamas cursor tipo duomenu loaderis, kurio vardas bus loaderId
        Log.d(TAG, "onCreateLoader() >> loaderID : " + loaderID); //loge parasomas CallLogs, onCreateLoader() >> loaderID : sukuriamo loaderio ID (loaderioID)
        if (loaderID >0) {//jeigu loaderID kintamasis tures reiksme daugiau nei 0, tai bus atliekami veiksmai zemiau skliaustuose
            return new CursorLoader(//returnins nauja cursor loaderi su zemiau pateiktais argumentais
                    this,   // Parent activity context
                    CallLog.Calls.CONTENT_URI,        // Table to query
                    null,     // Projection to return, null taip pat reiski, jog reiksme bus nenurodyta
                    null,            // No selection clause
                    null,            // No selection arguments
                    CallLog.Calls.DATE + " DESC" //sorting by date
            );
        } else {//jeigu loaderis netures reiksmes didesnes uz nuli arba ji bus lygi nuliui, tai bus atliekamas zemiau skliaustuose kodas
            return null; //grazins null, null yra 0 arba tuscia reiksme
        }


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor managedCursor) {//apsirasoma onLoadFinished funkcija, kuriai yra perduodamas anksciau sukurtas cursor loader ir kitas argumentas Cursor managedCursor
        Log.d(TAG, "onLoadFinished()"); //loge parasomas pranesimas CallLogs, onLoadFinished()

        StringBuilder sb = new StringBuilder(); //sukuriamas naujas StringBuilder, kuriam suteikiamas sb vardas

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER); // apsirasomas int (skaiciaus) tipo kintamasis number ir jam duodama numerio reiksme is skambuciu saraso duomenu lenteles
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE); // apsirasomas int (skaiciaus) tipo kintamasis type ir jam duodama skambucio tipo reiksme is skambuciu saraso duomenu lenteles
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE); // apsirasomas int (skaiciaus) tipo kintamasis date ir jam duodama datos reiksme is skambuciu saraso duomenu lenteles
        sb.append("<h4>Praleisti skambučiai <h4>"); //musu anksciau sukurto StringBuilder, kurio vardas yra sb yra keiciama reiksme ir jame yra idedama nauja eilute "Praleisti skambučiai", kuri yra formatuojama pasitelkiant <h4>, atsimink HTML
        sb.append("\n"); //musu anksciau sukurto StringBuilder, kurio vardas yra sb yra keiciama reiksme ir jame yra idedamas eilutes ilgio tarpas (tas pats, kaip spausti Enter wordo dokumente
        sb.append("\n"); //musu anksciau sukurto StringBuilder, kurio vardas yra sb yra keiciama reiksme ir jame yra idedamas eilutes ilgio tarpas (tas pats, kaip spausti Enter wordo dokumente

        sb.append("<table>"); //musu anksciau sukurto StringBuilder, kurio vardas yra sb yra keiciama reiksme ir jame yra pradedama lentele

        while (managedCursor.moveToNext()) { //pradedamas while ciklas, jis tol tesis, kol musu managedCursor gales judeti prie kito iraso duomenuBazes lenteleje, ir kol ciklas veiks tol bus kartojamas skliaustuose esantis kodas
            String phNumber = managedCursor.getString(number); //sukuriamas naujas String (zodzio) tipo kintamasis phNumber ir jam yra priskiriama numerio reiksme, kuri yra gaunama is duomenu lenteles ir konvertuojama i zodi is skaiciaus
            String callType = managedCursor.getString(type); //sukuriamas naujas String (zodzio) tipo kintamasis callType ir jam yra priskiriama skambucio tipo reiksme, kuri yra gaunama is duomenu lenteles ir konvertuojama i zodi is skaiciaus
            String callDate = managedCursor.getString(date); //sukuriamas naujas String (zodzio) tipo kintamasis callDate ir jam yra priskiriama skambucio datos reiksme, kuri yra gaunama is duomenu lenteles ir konvertuojama i zodi is skaiciaus
            Date callDayTime = new Date(Long.valueOf(callDate)); //sukuriamas naujas Date (datos) tipo kintamasis callDayTime, ir jam yra priskiriama callDate reiksme is 101 eilutes, kuri yra konvertuojama i datos formata
            String dir = null; //apsirasomas naujas String (zodzio) tipo kintamasis dir ir jam yra priskiriama tuscia reiksme (0)
            int callTypeCode = Integer.parseInt(callType); //apsirasomas naujas int (skaiciaus) tipo kintamasis callTypeCode, kurio reiksme bus 100 eilutes callType reiksme konvertuota is zodzio i skaiciu
            switch (callTypeCode) {//apsirasomas switch yra jungiklis, kaip kokios lemputes (ka daryti jei tam tikri kriterijai bus pasirinkti), kurio parametras bus callTypeCode kintamasis
                case CallLog.Calls.OUTGOING_TYPE: //apsirasomi case (kriterijai switchui)
                case CallLog.Calls.INCOMING_TYPE: //apsirasomi case (kriterijai switchui)
                    dir = "null"; //jeigu sie case bus kriterijais, tai dir reiksme bus null
                    break; //case yra uzbaigiami

                case CallLog.Calls.MISSED_TYPE: //apsirasomi case (kriterijai switchui)
                    dir = "Praleistas"; //jeigu sis case bus kriterijus, tai dir reiksme bus Praleistas
                    break; //case yra uzbaigiami
            }
            if (dir == "null") { //jeigu dir reiksme bus lygi null zodziui, tai nieko nedaryti
            } else {//jeigu dir reiksme bus kitka, o ne null zodis, tai atlikti zemiau pateikta koda
                sb.append("<tr>")//start of comment i anksciau apsirasyta StringBuilder, kurio vardas yra sb yra idedamas naujas formatuotas tekstas
                        .append("<td>Telefono numeris: </td>")
                        .append("<td><strong>")
                        .append(phNumber) //idedamas numeris skambinusio
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<tr>")
                        .append("<td>Skambučio tipas:</td>")
                        .append("<td><strong>")
                        .append(dir)//idedamas skambucio tipas
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<tr>")
                        .append("<td>Data ir laikas:</td>")
                        .append("<td><strong>")
                        .append(callDayTime)//idedama data skambucio
                        .append("</strong></td>");
                sb.append("</tr>");
                sb.append("<br/>");
                sb.append("<br/>");
            }
            sb.append("</table>");//end of comment uzdaroma lentele ir taip yra pildomas StringBuilder, kurio vardas yra sb iki tol kol nebebus praleistu skambuciu
        }
            managedCursor.close(); //uzdaromas musu sukurtas duomenu loaderis

            callLogsTextView.setText(Html.fromHtml(sb.toString())); //i musu pries, tai apsirasyta callLogsTextView yra irasomas visas StringBuilder, kurio vardas yra sb tekstas
        }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) { //apsirasoma nauja funckija onLoaderReset, kuriai yra perduodamas vienas argumentas Loader<Cursor> loader
        Log.d(TAG, "onLoaderReset()"); //loge yra irasomas pranesimas CallLogs, onLoaderReset()
        // do nothing
    }
}
