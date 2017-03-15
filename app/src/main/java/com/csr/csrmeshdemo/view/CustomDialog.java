/**   
 * @Title:CustomDialog.java
 * @Package com.huawei.smallRadar.view
 * @Description: 
 * @author 姚海军  
 * @date 2016年6月14日上午8:28:38
 * @version V1.0   
 * History :
 *  1. Yaohaijun add for the first release ,2016年6月14日  
 *
 * 
 * Copyright (C), Tonly electronics Holdincs Limited
 * All rights reserved
 ******************************************************************************/
package com.csr.csrmeshdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csr.csrmeshdemo.R;

import java.io.UnsupportedEncodingException;

/**
 * @author 陈家鸿
 *
 */
public class CustomDialog extends Dialog {
	private static Toast toast;

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private int color;
		private CharSequence text;
		public String message;
		private int cursorPos = 1;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		CustomDialog dialog;
		View relayout;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setEditText(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setTitle(CharSequence title) {
			this.text = title;
			return this;
		}

		public Builder setTitleColor(int color) {
			this.color = color;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}
		
		public void dialogDismiss() {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}

		public void loadFinish() {
			// 停止动画
			loadStop();
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}

		/**
		 * 加载结束
		 */
		public void loadStop() {
			// 停止动画
			if (contentView != null)
				contentView.postDelayed(new Runnable() {

					@Override
					public void run() {
						contentView.clearAnimation();
					}

				}, 200);

		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CustomDialog RenameCreate(final int length) {
			byte[] byd = {};
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			relayout = inflater.inflate(R.layout.dialog_rename_layout, null);
			dialog.addContentView(relayout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) relayout.findViewById(R.id.title)).setText(title);
			final EditText editText = (EditText) relayout
					.findViewById(R.id.editRename);
			editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					length) });
			// set the confirm button
			if (positiveButtonText != null) {
				((Button) relayout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) relayout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									message = editText.getText().toString();
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				relayout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) relayout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) relayout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				relayout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			try {
				byd = message.toString().getBytes("utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (message != null) {
				editText.setText(message);
				if (byd.length > length - 1) {
					editText.setSelection(editText.getText().toString().length());// 将光标追踪到内容的最后
					message = editText.getText().toString();
				} else {
					editText.setSelection(message.length());// 将光标追踪到内容的最后
				}
			}

			editText.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

					
					
				
					
					byte[] byd = {};
					try {
						byd = s.toString().getBytes("utf-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(s.toString().isEmpty()){
						((Button) relayout.findViewById(R.id.positiveButton)).setEnabled(false);
					}else{
						((Button) relayout.findViewById(R.id.positiveButton)).setEnabled(true);
					}

					
					if (byd.length > length - 1) {
						
						if (count >= 2) {// 表情符号的字符长度最小为2
							int pos = s.length() - count;
							CharSequence input = s.subSequence(pos, pos
							+ count);
							if (containsEmoji(input.toString())) {
								
								((Editable) s).delete(start, start + count);
								if (toast != null) {
									toast.cancel();
								}
								toast = Toast.makeText(context,
										R.string.text_length_alread_max,
										Toast.LENGTH_SHORT);
								toast.show();
								return;
							}
							
						}
						
						
						if (start == length - 1) {
							int pos = s.length() - 1;
							((Editable) s).delete(pos, pos + 1);
						} else {
							((Editable) s).delete(start, start + 1);
						}
						if (toast != null) {
							toast.cancel();
						}
						toast = Toast.makeText(context,
								R.string.text_length_alread_max,
								Toast.LENGTH_SHORT);
						toast.show();
					} else {
						message = s.toString();
					}
					


				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});

			// set the content message

			dialog.setContentView(relayout);
			return dialog;
		}

		public CustomDialog customCreate() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_lay, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title_dialog)).setText(title);
			if (color != 0) {
				((TextView) layout.findViewById(R.id.title_dialog))
						.setTextColor(color);
			}

			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.sure))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.sure))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sure).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.cancel))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.cancel))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.cancel).setVisibility(View.GONE);
			}

			// set the content message

			dialog.setContentView(layout);
			return dialog;
		}

		public CustomDialog CoolPlayCreate() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_lay, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title_dialog)).setText(text);

			// set the confirm button
			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.sure))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.sure))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.sure).setVisibility(View.GONE);
			}
			// set the cancel button
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.cancel))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.cancel))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.cancel).setVisibility(View.GONE);
			}

			// set the content message

			dialog.setContentView(layout);
			return dialog;
		}

		public CustomDialog loadindDialog() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dialog = new CustomDialog(context, R.style.Dialog);
			contentView = inflater
					.inflate(R.layout.progress_share_dialog, null);
			dialog.addContentView(contentView, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			((TextView) contentView.findViewById(R.id.loading_title))
					.setText(title);
			dialog.setContentView(contentView);
			
			return dialog;
		}

	}
	
	/**
	* 判断是否是Emoji
	* 
	* @param codePoint
	*            比较的单个字符
	* @return
	*/
	private static boolean isEmojiCharacter(char codePoint) {
	return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
	|| (codePoint == 0xD)
	|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
	|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
	|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
	}
	
	/**
	* 检测是否有emoji表情
	* 
	* @param source
	* @return
	*/
	public static boolean containsEmoji(String source) {
	int len = source.length();
	for (int i = 0; i < len; i++) {
	char codePoint = source.charAt(i);
	if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
	return true;
	}
	}
	return false;
	}

}
