package io.egorwhite.zaprett.ui.hosts;

import static java.net.URI.*;
import static io.egorwhite.zaprett.R.drawable.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import androidx.annotation.RequiresApi;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import io.egorwhite.zaprett.MainActivity;
import io.egorwhite.zaprett.ModuleInteractor;
import io.egorwhite.zaprett.R;
import io.egorwhite.zaprett.R.drawable;
import io.egorwhite.zaprett.databinding.FragmentHostsBinding;

public class HostsFragment extends Fragment {

	private FragmentHostsBinding binding;
	public static LinearLayout listLayout;
	public FloatingActionButton addfab;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		HostsViewModel HostsViewModel =
			new ViewModelProvider(this).get(HostsViewModel.class);

		binding = FragmentHostsBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
		listLayout = root.findViewById(R.id.listlayout);
		addfab = root.findViewById(R.id.addfab);
addfab.setOnClickListener(view -> {
			if (Environment.isExternalStorageManager()) {
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("text/*");
				Intent chooser = Intent.createChooser(intent, "Выберите файл листа");
				startActivityForResult(chooser, 228);
			} else {
			    Snackbar.make(root.getRootView(), R.string.snack_no_storage, Snackbar.LENGTH_SHORT).show();
			}
		});

		if (Environment.isExternalStorageManager()) {
			String[] allLists = ModuleInteractor.getAllLists();
			String[] activeLists = ModuleInteractor.getActiveLists();
			for (int i = 0; i < allLists.length; i++) {
				String list = allLists[i];
				if (list != null && !list.isEmpty()) {
					SwitchMaterial switchMaterial = new SwitchMaterial(root.getContext());
					CharSequence seq;
					if (MainActivity.settings.getBoolean("show_full_path", true)) {
						seq = list;
					} else {
						seq = list.split("/")[list.split("/").length - 1];
					}
					switchMaterial.setText(seq);
					for (String actlist : activeLists) {
						if (actlist.contains(list)) {
							switchMaterial.setChecked(true);
							Log.i("Enabled switch", "Enabled switch for " + list);
							break;
						}
					}
					switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
						Snackbar.make(root.getRootView(), R.string.pls_restart_snack, Snackbar.LENGTH_SHORT).show();
						if (b) ModuleInteractor.enableList(list);
						else ModuleInteractor.disableList(list);
					});
					listLayout.addView(switchMaterial);
					Log.i("Added element", "Added switch for " + list);
				}
			}
		}
else {
    new MaterialAlertDialogBuilder(root.getContext())
                    .setTitle(R.string.error_no_storage_title)
                    .setMessage(R.string.snack_no_storage)
                    .setPositiveButton(R.string.btn_continue, new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.R)
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .show();
}
		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
								 Intent resultData) {
		if (requestCode == 228
				&& resultCode == Activity.RESULT_OK) {
			Uri uri = null;
			if (resultData != null) {
				uri = resultData.getData();
				copyFileToDirectory(this.getContext(), uri, ModuleInteractor.getZaprettPath() + "/lists");
			}
		}
	}

	//ДАЛЬШЕ ПИЗДЕЦ!!!!
	//ИИ-СГЕНЕРИРОВАНО
	public static File copyFileToDirectory(Context context, Uri uri, String destinationDirectory) {
		File destinationFile = null;
		try {
			// Создаем директорию, если она не существует
			File dir = new File(destinationDirectory);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			// Получаем имя файла из Uri
			String fileName = getFileName(context, uri);
			if (fileName == null) {
				fileName = "file_" + System.currentTimeMillis(); // Если имя файла не удалось получить, создаем уникальное имя
			}

			// Создаем файл в целевой директории
			destinationFile = new File(dir, fileName);
			if (destinationFile.exists()) {
				destinationFile.delete();
			}

			// Копируем данные из Uri в целевой файл
			InputStream inputStream = context.getContentResolver().openInputStream(uri);
			OutputStream outputStream = new FileOutputStream(destinationFile);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}

			outputStream.flush();
			outputStream.close();
			inputStream.close();

			Log.d("FileUtils", "File copied successfully to " + destinationFile.getAbsolutePath());

			String[] activeLists = ModuleInteractor.getActiveLists();
			SwitchMaterial switchMaterial = new SwitchMaterial(listLayout.getContext());
			CharSequence seq;
			String list = ModuleInteractor.getZaprettPath() + "/lists/" + fileName;
			if (MainActivity.settings.getBoolean("show_full_path", true)) {
				seq = list;
			} else {
				seq = fileName;
			}
			switchMaterial.setText(seq);
			for (String actlist : activeLists) {
				if (actlist.contains(list)) {
					switchMaterial.setChecked(true);
					Log.i("Enabled switch", "Enabled switch for " + list);
					break;
				}
			}
			switchMaterial.setOnCheckedChangeListener((compoundButton, b) -> {
				Snackbar.make(listLayout.getRootView(), R.string.pls_restart_snack, Snackbar.LENGTH_SHORT).show();
				if (b) ModuleInteractor.enableList(list);
				else ModuleInteractor.disableList(list);
			});
			listLayout.addView(switchMaterial);

		} catch (Exception e) {
			Log.e("FileUtils", "Error copying file", e);
		}

		return destinationFile;
	}

	private static String getFileName(Context context, Uri uri) {
		String result = null;

		// Если Uri является "content"
		if (uri.getScheme().equals("content")) {
			try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
				if (cursor != null && cursor.moveToFirst()) {
					// Пытаемся получить имя файла из столбца DISPLAY_NAME
					int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
					if (nameIndex != -1) { // Проверяем, что столбец существует
						result = cursor.getString(nameIndex);
					}
				}
			} catch (Exception e) {
				Log.e("FileUtils", "Error getting file name from Uri", e);
			}
		}

		// Если имя файла не удалось получить через курсор, извлекаем его из пути Uri
		if (result == null) {
			result = uri.getPath();
			if (result != null) {
				int cut = result.lastIndexOf('/');
				if (cut != -1) {
					result = result.substring(cut + 1);
				}
			}
		}

		return result;
	}
}
