package com.ittianyu.dynamicupdater.loader;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import com.ittianyu.dynamicupdater.loader.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class ResourceLoader {
//    public static final String UTF_8 = "utf-8";
    private ClassLoader classLoader;
    private Map<String, JSONObject> jsonMap = new HashMap<>();

    private static WeakHashMap<ClassLoader, ResourceLoader> map = new WeakHashMap<>();

    public static ResourceLoader getInstance(ClassLoader classLoader) {
        ResourceLoader resourceLoader = map.get(classLoader);
        if (null == resourceLoader) {
            resourceLoader = new ResourceLoader(classLoader);
            map.put(classLoader, resourceLoader);
        }
        return resourceLoader;
    }

    public ResourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    // getString

    public String getString(String name) {
        List<String> list = ResourceUtils.getStringsPathList();
        for (String path : list) {
            String string = getString(path, name);
            if (string != null) {
                return string;
            }
        }
        return null;
    }

    private String getString(String path, String name) {
        try {
            JSONObject json = getJson(path);
            if (json == null) {
                return null;
            }
            return json.getString(name);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // get color

    public Integer getColor(String name) {
        List<String> list = ResourceUtils.getColorsPathList();
        for (String path : list) {
            Integer color = getColor(path, name);
            if (color != null) {
                return color;
            }
        }
        return null;
    }

    private Integer getColor(String path, String name) {
        try {
            JSONObject json = getJson(path);
            if (json == null) {
                return null;
            }
            return Color.parseColor(json.getString(name));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject getJson(String path) throws JSONException {
        JSONObject jsonObject = jsonMap.get(path);
        if (null == jsonObject) {
            InputStream in = classLoader.getResourceAsStream(path);
            String json = ResourceUtils.toString(in);
            if (null == json) {
                return null;
            }
            jsonObject = new JSONObject(json);
            jsonMap.put(path, jsonObject);
        }
        return jsonObject;
    }

    // getDrawable
    public Drawable getDrawable(Context context, String name) {
        List<String> list = ResourceUtils.getDrawablePathList(context);
        for (String path : list) {
            InputStream in = classLoader.getResourceAsStream(path + File.separator + name);
            if (in == null) {
                continue;
            }

            try {
                if (name.endsWith(".xml")) {
//                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//                    XmlPullParser parser = factory.newPullParser();
//                    parser.setInput(in, UTF_8);
//                    return Drawable.createFromXml(context.getResources(), parser);
                    return null;
                } else {
                    return Drawable.createFromResourceStream(context.getResources(), null, in, name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
