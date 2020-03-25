package com.lykke.tests.suites;

import static com.lykke.tests.api.common.CommonConsts.SMOKE_TEST;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages("com.lykke.tests")
@IncludeTags(SMOKE_TEST)
public class SmokeTests {

}

