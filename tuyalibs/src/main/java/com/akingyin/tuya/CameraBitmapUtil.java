package com.akingyin.tuya;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 照相图片处理工具类
 * 
 * @author king
 * 
 */
public class CameraBitmapUtil {

	public static final int NormWidth = 540;
	public static final int NormHigth = 960;

	/**
	 * // 最大偏移量
	 */
	public static final int MaxpOffset = 40;

	/**
	 * // 保存图片的质量
	 */
	public static final int quality = 90;

	/**
	 * 创建一个缩放的图片
	 * 
	 * @param path
	 *            图片地址
	 * @param w
	 *            图片宽度
	 * @param h
	 *            图片高度
	 * @return 缩放后的图片
	 */
	public static Bitmap createBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
			BitmapFactory.decodeFile(path, opts);
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			// 缩放的比例
			double ratio = 0.0;
			if (srcWidth < w || srcHeight < h) {
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			} else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double) srcWidth / w;
				destWidth = w;
				destHeight = (int) (srcHeight / ratio);
			} else {
				ratio = (double) srcHeight / h;
				destHeight = h;
				destWidth = (int) (srcWidth / ratio);
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
			newOpts.inSampleSize = (int) ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			return BitmapFactory.decodeFile(path, newOpts);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * 
	 * @param data
	 *            数据
	 * @param path
	 *            路径
	 * @param img
	 *            图片名
	 * @param rotat
	 *            旋转角度
	 * @return
	 */
	public static Bitmap dataToBaseBitmap(byte[] data, String path, String img,
			int rotat) {

		Bitmap img_src = null;

		FileOutputStream fos = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			fos = new FileOutputStream(new File(path, img));
			img_src = BitmapFactory.decodeByteArray(data, 0, data.length);

			// img_src.compress(Bitmap.CompressFormat.JPEG, 80, fos);
			// if(img_src.getWidth() > img_src.getHeight()){
			// img_src = createBitmap(path+img, maxWidth, maxHigth);
			// }else{
			// img_src = createBitmap(path+img, maxHigth, maxWidth);
			// }
			//
			Matrix m = new Matrix();
			m.setRotate(rotat);

			img_src = Bitmap.createBitmap(img_src, 0, 0, img_src.getWidth(),
					img_src.getHeight(), m, true);


			img_src.compress(Bitmap.CompressFormat.JPEG, quality, fos);

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		} catch (Error e) {
			if (null != img_src) {
				img_src.recycle();
				img_src = null;
			}

			return null;
		} finally {

			if (null != fos) {

				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return img_src;

	}

	/**
	 * 压缩图片至 960*540
	 * 
	 * @param mBitmap
	 * @param rotat
	 * @param rect
	 * @param path
	 * @param img
	 * @return
	 */
	public static boolean zipImageTo960x540(Bitmap mBitmap, int rotat,
			Rect rect, String path, String img,long  time) {
		if (null == mBitmap) {
			return false;
		}
		Bitmap bitmap = null;
		Rect dst = null;
		FileOutputStream fos = null;
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}

			mBitmap = Bitmap.createBitmap(mBitmap, rect.left, rect.top,
					rect.width(), rect.height());

			if (mBitmap.getWidth() < mBitmap.getHeight()) {
				bitmap = Bitmap.createBitmap(NormWidth, NormHigth,
						Bitmap.Config.RGB_565);
				dst = new Rect(0, 0, NormWidth, NormHigth);
			} else {
				bitmap = Bitmap.createBitmap(NormHigth, NormWidth,
						Bitmap.Config.RGB_565);
				dst = new Rect(0, 0, NormHigth, NormWidth);
			}

			Canvas canvas = new Canvas(bitmap);
			Rect src = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
			canvas.drawBitmap(mBitmap, src, dst, new Paint());
			canvas = new Canvas(bitmap);
			TextPaint paint = new TextPaint();
			paint.setTextSize(20f);
			paint.setColor(Color.RED);
			canvas.drawText(getNowDate(time), bitmap.getWidth() - 210,
					bitmap.getHeight() - 10, paint);
			canvas.save();

			file = new File(path, img);
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (null != fos) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (null != bitmap) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		return true;
	}

	// 创建文件夹
	public static boolean CreateMkdir(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				return file.mkdirs();
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean SysCameraZipImage(String path,long  time) {
		int degree = 0;
		FileInputStream fis = null;
		Bitmap mbitmap = null;

		try {
			fis = new FileInputStream(path);
			FileDescriptor fd = fis.getFD();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(fd, null, options);
			int width = options.outWidth;
			int height = options.outHeight;

			if (width > height) {
				width = options.outHeight;
				height = options.outWidth;
			}
			int outWidth = Math.round(options.outWidth / NormWidth);
			int outHeight = Math.round(options.outHeight / NormHigth);

			if (outWidth > 1 && outHeight > 1) {
				if (outWidth >= outHeight) {
					options.inSampleSize = outHeight;
				} else {
					options.inSampleSize = outWidth;
				}
			}
			degree = readPictureDegree(path);

			options.inJustDecodeBounds = false;
			mbitmap = BitmapFactory.decodeStream(fis, null, options);

			if (degree > 0) {
				Matrix matrix = new Matrix();
				matrix.reset();
				// 设置图片旋转角度
				matrix.postRotate(degree);
				// 创建新的图片
				mbitmap = Bitmap.createBitmap(mbitmap, 0, 0,
						mbitmap.getWidth(), mbitmap.getHeight(), matrix, true);
			} else {
				mbitmap = mbitmap.copy(Bitmap.Config.RGB_565, true);
			}

			if (mbitmap.getWidth() - NormHigth > MaxpOffset
					|| mbitmap.getHeight() - NormHigth > MaxpOffset) {

				Matrix matrix = new Matrix();

				float sx = 0f;
				if (mbitmap.getWidth() > mbitmap.getHeight()) {
					sx = (float) (NormHigth / mbitmap.getWidth());
				} else {
					sx = (float) (NormHigth / mbitmap.getHeight());
				}
				matrix.postScale(sx, sx);
				// 长和宽放大缩小的比例

				mbitmap = Bitmap.createBitmap(mbitmap, 0, 0,
						mbitmap.getWidth(), mbitmap.getHeight(), matrix, true);

			}
			Canvas canvas = new Canvas(mbitmap);
			TextPaint paint = new TextPaint();
			paint.setTextSize(20f);
			paint.setColor(Color.RED);
			canvas.drawText(getNowDate(time), mbitmap.getWidth() - 190, 30f, paint);
			canvas.save();

			FileOutputStream fos = new FileOutputStream(path);

			mbitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} catch (Error e) {
			e.printStackTrace();
			return false;
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != mbitmap) {
				mbitmap.recycle();
				mbitmap = null;
			}
		}
		return true;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
				default:
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片
	 * 
	 * @param bitmap
	 * @param degree
	 *            旋转的角度
	 * @return
	 */
	public static Bitmap spinPicture(Bitmap bitmap, int degree) {
		if (null == bitmap) {
			return null;
		}
		try {
			Matrix matrix = new Matrix();
			matrix.reset();
			// 设置图片旋转角度
			matrix.postRotate(degree);
			// 创建新的图片

			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} catch (Error e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
     * 获取缩小的比例(主要用于涂鸦显示)
     * 
     * @param bitmap
     * @param dm
     * @return
     */
    public static float  minScale = 7/8f;
    public static float getBitmapScale(Bitmap bitmap, DisplayMetrics dm) {

        if (null == bitmap || null == dm) {
            return 0;
        }
        int width = bitmap.getWidth();
        int heigth = bitmap.getHeight();
        if(width > heigth){
        	width = heigth;
        	heigth = bitmap.getWidth();
        }
			int  screenWidth = Math.min(dm.widthPixels,dm.heightPixels);
			int  screenHight = Math.max(dm.widthPixels,dm.heightPixels);
     // 当前图片比屏幕大
        if (width > screenWidth|| heigth > screenHight) {
            float x = (screenWidth - MaxpOffset) / (float) width;
            float y = (screenHight- MaxpOffset) / (float) heigth;
            if (x <= y) {
                return x;
            }
            return y;
        }

        // 　图片至少应占屏幕的4/5
        if (width * (1/minScale) < screenWidth || heigth * (1/minScale) < screenHight) {
            float x = (float) screenWidth * minScale / width;
            float y = (float) screenHight * minScale / heigth;
					  System.out.println("x="+x+":y="+y);
            return Math.min(x, y);
        }
     

        return 0;
    }

	/**
	 * 缩放Bitmap
	 * 
	 * @param bitmap
	 * @param scale
	 * @return
	 */
	public static Bitmap BitmapScale(Bitmap bitmap, float scale) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String getNowDate(long  time) {
		return sdf.format(new Date(time));
	}
}
