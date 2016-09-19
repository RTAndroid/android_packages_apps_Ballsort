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

package rtandroid.ballsort.hardware;

public class MockStepper extends Stepper
{
    public boolean mDidSteps = false;
    public boolean mWasCancelled = false;

    public MockStepper()
    {
        super("MockStepper", 0, 0, 0, 0);
    }

    @Override
    public void cancel()
    {
        mWasCancelled = true;
    }

    @Override
    public void doSteps(int[] steps)
    {
        mDidSteps = true;
    }
}
