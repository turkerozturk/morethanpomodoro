/*
 * This file is part of the MoreThanPomodoro project.
 * Please refer to the project's README.md file for additional details.
 * https://github.com/turkerozturk/morethanpomodoro
 *
 * Copyright (c) 2025 Turker Ozturk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.en.html>.
 */
package org.example.thirdparty.utils;
/*
 * This file is copied from the Musicplayer project
 * (https://github.com/Velliz/Musicplayer), specifically from the commit:
 * https://github.com/Velliz/Musicplayer/commit/41c4c5ee21a21a845865c34c2b847c75d3349604
 *
 * The original code is licensed under the Apache License Version 2.0,
 * January 2004 (http://www.apache.org/licenses/).
 *
 * As per the requirements of the Apache License, the original copyright
 * notice, license text, and disclaimer must be retained:
 *
 *  --- Begin Apache License Notice ---
 *  Copyright (c) 2017 Velliz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  --- End Apache License Notice ---
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutor {

    //This executor is suitable for applications that launch many short-lived tasks.
    private static ExecutorService backgroundEx = Executors.newCachedThreadPool(); //UI thread shouldn't do math

    //pool with fixed number of "parallel" thread
    //private static int NTHREAD = Runtime.getRuntime().availableProcessors();
    //private static ExecutorService backgroundEx = Executors.newFixedThreadPool(NTHREAD); //UI thread shouldn't do math
    public BackgroundExecutor() {
    }

    public static ExecutorService get() {
        return backgroundEx;
    }
}
