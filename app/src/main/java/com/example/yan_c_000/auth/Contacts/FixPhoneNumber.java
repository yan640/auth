package com.example.yan_c_000.auth.Contacts;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by Yan-Desktop on 13.01.2018.
 */

public class FixPhoneNumber {

    public static String FixPhoneNumber(Context ctx, String rawNumber)
    {
        String      fixedNumber = "";

        // get current location iso code
        TelephonyManager telMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String              curLocale = telMgr.getNetworkCountryIso().toUpperCase();

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(ctx);
        Phonenumber.PhoneNumber     phoneNumberProto;

        // gets the international dialling code for our current location
        String              curDCode = String.format("%d", phoneUtil.getCountryCodeForRegion(curLocale));
        String              ourDCode = "";

        if(rawNumber.indexOf("+") == 0)
        {
            int     bIndex = rawNumber.indexOf("(");
            int     hIndex = rawNumber.indexOf("-");
            int     eIndex = rawNumber.indexOf(" ");

            if(bIndex != -1)
            {
                ourDCode = rawNumber.substring(1, bIndex);
            }
            else if(hIndex != -1)
            {
                ourDCode = rawNumber.substring(1, hIndex);
            }
            else if(eIndex != -1)
            {
                ourDCode = rawNumber.substring(1, eIndex);
            }
            else
            {
                ourDCode = curDCode;
            }
        }
        else
        {
            ourDCode = curDCode;
        }

        try
        {
            phoneNumberProto = phoneUtil.parse(rawNumber, curLocale);
        }

        catch (NumberParseException e)
        {
            return rawNumber;
        }

//        if(curDCode.compareTo(ourDCode) == 0)
//            fixedNumber = phoneUtil.format(phoneNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
//        else
            fixedNumber = phoneUtil.format(phoneNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        fixedNumber = PhoneNumberUtils.formatNumber(fixedNumber.replaceAll("[ -()]", ""));

        //todo Check number is internationall, if not don't send it in DB
        return fixedNumber.replace(" ", "");
    }
}
