package io.github.cgew85.picops.view;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

import io.github.cgew85.picops.R;
import io.github.cgew85.picops.controller.BitmapWorkerTask;
import io.github.cgew85.picops.controller.FileHandlingController;
import io.github.cgew85.picops.controller.SimpleCounterForTempFileName;

/**
 * Fragment 1 will be used to display the currently used image
 */
public class Fragment1 extends Fragment {

    public ImageView workingImageView;
    public View rootView;
    public Button buttonfilter;
    private int fragmentWidth;
    private int fragmentHeight;
    SimpleCounterForTempFileName counter = SimpleCounterForTempFileName.getInstance();

    @Override
    public void onResume() {
        super.onResume();
        //workingImageView.setImageBitmap(null);
        //TODO: Testen
        onActivityCreated(this.getArguments());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        workingImageView.setImageBitmap(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        workingImageView.setImageBitmap(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        workingImageView.setImageBitmap(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment1, container, false);
        workingImageView = (ImageView) rootView.findViewById(R.id.workingimageview);
        buttonfilter = (Button) rootView.findViewById(R.id.buttonapplychanges);
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO: Verbessern wenn m�glich
        final RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.containerimage);

        rootView.post(new Runnable() {

            @Override
            public void run() {

                fragmentWidth = rl.getWidth();
                fragmentHeight = rl.getHeight();


                BitmapWorkerTask task = new BitmapWorkerTask(workingImageView);
                Log.d("INFO", "fragmentWidth: " + fragmentWidth);
                Log.d("INFO", "fragmentHeight: " + fragmentHeight);

                Log.d("INFO", "Counter: " + counter.getCounter());

                if (FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(rootView.getContext(), 0).equals("")) {
                    String[] params = {FileHandlingController.getInstance().returnAbsoluteFilePath(rootView.getContext()), String.valueOf(fragmentWidth), String.valueOf(fragmentHeight)};
                    task.execute(params);
                } else {
                    String[] params = {FileHandlingController.getInstance().returnAbsoluteFilePathWorkingCopy(rootView.getContext(), counter.getCounter() - 1), String.valueOf(fragmentWidth), String.valueOf(fragmentHeight)};
                    task.execute(params);
                }
            }
        });

        buttonfilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /** Navigation zu TabActivity **/
                buttonfilter.setBackgroundResource(R.drawable.buttononclick);
                buttonfilter.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                Intent intent = new Intent(rootView.getContext(), FilterEffectsTabActivity.class);
                intent.putExtra("fragmentWidth", fragmentWidth);
                intent.putExtra("fragmentHeight", fragmentHeight);
                Log.d("INFO", "Dim. Frag. fragmentWidth: " + fragmentWidth + " fragmentHeight: " + fragmentHeight);
                startActivity(intent);
                buttonfilter.setBackgroundResource(R.drawable.button);
            }
        });
    }

    private boolean checkForWorkingDirectory() {
        boolean workingDirectoryExists = false;

        File directoryForPicOps = new File(Environment.getExternalStorageDirectory().toString().concat("/picOps"));
        if (directoryForPicOps.isDirectory()) {
            if (directoryForPicOps.list().length > 0) {
                workingDirectoryExists = true;
            }
        }

        return workingDirectoryExists;
    }

    public void hideContentWhileSaving() {
        workingImageView.setImageBitmap(null);
    }
}
