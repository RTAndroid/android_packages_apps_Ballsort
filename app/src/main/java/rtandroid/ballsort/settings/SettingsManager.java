/*
 * Copyright (C) 2016 RTAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rtandroid.ballsort.settings;

public class SettingsManager
{
    private final static Settings mSettings = new Settings();
    private static final DataState mData = new DataState();

    /**
     * Getter for settings instance.
     *
     * @return singleton settings object.
     */
    public static Settings getSettings()
    {
        return mSettings;
    }

    /**
     * Getter for data instance.
     *
     * @return singleton data object.
     */
    public static DataState getData()
    {
        return mData;
    }

}
