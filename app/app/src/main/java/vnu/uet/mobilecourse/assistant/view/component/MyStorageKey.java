package vnu.uet.mobilecourse.assistant.view.component;

import com.bumptech.glide.load.Key;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

import androidx.annotation.NonNull;

public class MyStorageKey implements Key {

    private StorageReference mRef;

    public MyStorageKey(StorageReference ref) {
        mRef = ref;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest digest) {
        mRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                long updatedTime = storageMetadata.getUpdatedTimeMillis();
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(updatedTime);
                digest.update(buffer.array());
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyStorageKey key = (MyStorageKey) o;

        return mRef.equals(key.mRef);
    }

    @Override
    public int hashCode() {
        return mRef.hashCode();
    }
}