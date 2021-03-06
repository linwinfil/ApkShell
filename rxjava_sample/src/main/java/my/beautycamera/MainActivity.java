package my.beautycamera;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableOperator;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity@@";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button_test);
        button.setOnClickListener(v -> rxJavaTest2((Button) v));

        findViewById(R.id.button_string_fromat_test).setOnClickListener(v -> stringFormatTest());

        findViewById(R.id.button_add_pinned_shortcut).setOnClickListener(v -> addPinnedShortCut());

        findViewById(R.id.button_src_compare_test).setOnClickListener(v -> testStream());

        findViewById(R.id.btn_test_rx_java).setOnClickListener(v -> testRxJava());
    }

    public static class MainRun {
        public Object object;

        public MainRun() {
        }

        public MainRun(Object object) {
            this.object = object;
        }
    }

    private static boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

    public static void testRxJava() {

        Observable<MainRun> objectObservable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                System.out.println("crate subscribe:" + isMainThread());
                Object object = new Object();
                if (object != null) {
                    emitter.onNext(object);
                } else  {
                    emitter.onError(new NullPointerException());
                }
            }
        }).map(new Function<Object, MainRun>() {
            @Override
            public MainRun apply(Object o) throws Exception {
                System.out.println("map apply:" + isMainThread());
                return new MainRun(o);
            }
        }).onErrorResumeNext(new Function<Throwable, ObservableSource<MainRun>>() {
            @Override
            public ObservableSource<MainRun> apply(Throwable throwable) throws Exception {
                System.out.println("onErrorResumeNext apply:" + isMainThread());
                throwable.printStackTrace();
                return Observable.just(new MainRun());
            }
        }).subscribeOn(Schedulers.io());

        objectObservable.observeOn(Schedulers.io()).subscribe(new Observer<MainRun>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println(d);
            }

            @Override
            public void onNext(MainRun mainRun) {
                System.out.println(mainRun);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("complete");
            }
        });
    }

    void testRxJava2() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                int num = 0;
                for (int i = 0; i < 100; i++) {
                    num += i;
                }
                emitter.onNext(num);
                emitter.onComplete();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("is main thread:" + (Thread.currentThread() == Looper.getMainLooper().getThread()));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    void testStream()
    {
        ArrayList<Integer> intArr = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            intArr.add(i);
        }

        Optional<Integer> first = intArr.stream().filter(integer -> integer == 13).findFirst();
        Log.d(TAG, "MainActivity --> testStream: " + first);
    }

    void addPinnedShortCut()
    {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        if (shortcutManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (shortcutManager.isRequestPinShortcutSupported()) {
                    //添加已有的shortcut到桌面
                    ShortcutInfo pinShortcutInfo = new ShortcutInfo.Builder(this, "goto_web").build();
                    Intent shortcutResultIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo);
                    PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, shortcutResultIntent, 0);
                    shortcutManager.requestPinShortcut(pinShortcutInfo, broadcast.getIntentSender());
                }
            }
        }
    }

    void rxJavaTest()
    {
        //被观察者被观察者
        Observable<String> observable = Observable.unsafeCreate(subscriber -> {
            subscriber.onNext("a");
            subscriber.onNext("b");
            subscriber.onNext("c");
            subscriber.onComplete();
        });


        //观察者订阅
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onError(Throwable e)
            {
                Log.d(TAG, "MainActivity --> onError: ");
                e.printStackTrace();
            }

            @Override
            public void onComplete()
            {
                Log.d(TAG, "MainActivity --> onCompleted: ");
            }

            @Override
            public void onSubscribe(Disposable d)
            {
                Log.d(TAG, "MainActivity --> onSubscribe: ");
            }

            @Override
            public void onNext(String s)
            {
                Log.d(TAG, "MainActivity --> onNext: " + s);
            }
        };

        //被观察者2
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onError(Throwable e)
            {
            }

            @Override
            public void onComplete()
            {

            }

            @Override
            public void onSubscribe(Subscription s)
            {
                Log.d(TAG, "MainActivity --> onSubscribe: ");
            }

            @Override
            public void onNext(String s)
            {
            }
        };

        observable.subscribe(observer);
    }

    void rxJavaTest2(Button button)
    {
        class Test {
            String title = "";

            ArrayList<String> subTitles = new ArrayList<>();
        }
        ArrayList<String> subTitls = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            subTitls.add("" + i);
        }
        ArrayList<Test> tests = new ArrayList<>();
        Test test = new Test();
        test.title = "aa";
        test.subTitles = subTitls;
        tests.add(test);

        test = new Test();
        test.title = "bb";
        test.subTitles = subTitls;
        tests.add(test);

        test = new Test();
        test.title = "ccc";
        test.subTitles = subTitls;
        tests.add(test);

        test = new Test();
        test.title = "dddd";
        test.subTitles = subTitls;
        tests.add(test);


        Observable.fromArray(tests.toArray()).map(o -> ((Test) o).title).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d)
            {

            }

            @Override
            public void onNext(String s)
            {
                Log.d(TAG, "MainActivity --> onNext: " + s);
            }

            @Override
            public void onError(Throwable e)
            {

            }

            @Override
            public void onComplete()
            {

            }
        });

        Observable.fromArray(tests.toArray()).flatMap(o -> Observable.fromArray(((Test) o).subTitles.toArray())).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d)
            {

            }

            @Override
            public void onNext(Object o)
            {
                Log.d(TAG, "MainActivity --> onNext: " + o);
            }

            @Override
            public void onError(Throwable e)
            {

            }

            @Override
            public void onComplete()
            {

            }
        });

        Observable.fromArray(tests.toArray()).flatMap(o -> Observable.fromArray(((Test) o).subTitles.toArray())).lift((ObservableOperator<Integer, Object>) observer -> new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d)
            {
                observer.onSubscribe(d);
            }

            @Override
            public void onNext(Object o)
            {
                observer.onNext(Integer.parseInt((String) o));
            }

            @Override
            public void onError(Throwable e)
            {
                observer.onError(e);
            }

            @Override
            public void onComplete()
            {
                observer.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d)
            {

            }

            @Override
            public void onNext(Integer integer)
            {
                Log.d(TAG, String.format("MainActivity --> onNext: %s", integer));
                button.setText(integer + " @@");
            }

            @Override
            public void onError(Throwable e)
            {

            }

            @Override
            public void onComplete()
            {

            }
        });
    }

    void stringFormatTest()
    {
        System.out.println(Float.valueOf(String.format(Locale.CHINA, "%.2f", 16 / 9f)));
    }

}
