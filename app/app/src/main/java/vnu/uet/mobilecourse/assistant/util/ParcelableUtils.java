package vnu.uet.mobilecourse.assistant.util;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableUtils {

    public static byte[] toBytes(Parcelable parcelable) {
        Parcel parcel = Parcel.obtain();

        parcelable.writeToParcel(parcel, 0);

        byte[] result=parcel.marshall();

        parcel.recycle();

        return result;
    }

    public static <T> T toParcelable(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel=Parcel.obtain();

        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);

        T result=creator.createFromParcel(parcel);

        parcel.recycle();

        return result;
    }
}
