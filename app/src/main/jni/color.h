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

#define I2CBUS "/dev/i2c-10"

#define	TCS34725_ADDRESS			(0x29)
#define	TCS34725_COMMAND_BIT		(0x80)

#define	TCS34725_ENABLE				(0x00)
#define	TCS34725_ENABLE_AIEN		(0x10)	/* RGBCInterruptEnable */
#define	TCS34725_ENABLE_WEN			(0x08)	/* Waitenable-Writing1activatesthewaittimer */
#define	TCS34725_ENABLE_AEN			(0x02)	/* RGBCEnable-Writing1activestheADC,0disablesit */
#define	TCS34725_ENABLE_PON			(0x01)	/* Poweron-Writing1activatestheinternaloscillator,0disablesit */
#define	TCS34725_ATIME				(0x01)	/* Integrationtime */
#define	TCS34725_WTIME				(0x03)	/* Waittime			(ifTCS34725_ENABLE_WENisasserted) */
#define	TCS34725_WTIME_2_4MS		(0xFF)	/* WLONG0 = 2.4msWLONG1 = 0.029s */
#define	TCS34725_WTIME_204MS		(0xAB)	/* WLONG0 = 204msWLONG1 = 2.45s */
#define	TCS34725_WTIME_614MS		(0x00)	/* WLONG0 = 614msWLONG1 = 7.4s */
#define	TCS34725_AILTL				(0x04)	/* Clearchannellowerinterruptthreshold */
#define	TCS34725_AILTH				(0x05)
#define	TCS34725_AIHTL				(0x06)	/* Clearchannelupperinterruptthreshold */
#define	TCS34725_AIHTH				(0x07)
#define	TCS34725_PERS				(0x0C)	/* Persistenceregister-basicSWfilteringmechanismforinterrupts */
#define	TCS34725_PERS_NONE			(0b0000)	/* EveryRGBCcyclegeneratesaninterrupt */
#define	TCS34725_PERS_1_CYCLE		(0b0001)	/* 1cleanchannelvalueoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_2_CYCLE		(0b0010)	/* 2cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_3_CYCLE		(0b0011)	/* 3cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_5_CYCLE		(0b0100)	/* 5cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_10_CYCLE		(0b0101)	/* 10cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_15_CYCLE		(0b0110)	/* 15cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_20_CYCLE		(0b0111)	/* 20cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_25_CYCLE		(0b1000)	/* 25cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_30_CYCLE		(0b1001)	/* 30cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_35_CYCLE		(0b1010)	/* 35cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_40_CYCLE		(0b1011)	/* 40cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_45_CYCLE		(0b1100)	/* 45cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_50_CYCLE		(0b1101)	/* 50cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_55_CYCLE		(0b1110)	/* 55cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_PERS_60_CYCLE		(0b1111)	/* 60cleanchannelvaluesoutsidethresholdrangegeneratesaninterrupt */
#define	TCS34725_CONFIG				(0x0D)
#define	TCS34725_CONFIG_WLONG		(0x02)	/* Choosebetweenshortandlong			(12x)waittimesviaTCS34725_WTIME */
#define	TCS34725_CONTROL			(0x0F)	/* Setthegainlevelforthesensor */
#define	TCS34725_ID					(0x12)	/* 0x44 = TCS34721/TCS34725,0x4D = TCS34723/TCS34727 */
#define	TCS34725_STATUS				(0x13)
#define	TCS34725_STATUS_AINT		(0x10)	/* RGBCCleanchannelinterrupt */
#define	TCS34725_STATUS_AVALID		(0x01)	/* IndicatesthattheRGBCchannelshavecompletedanintegrationcycle */
#define	TCS34725_CDATAL				(0x14)	/* Clearchanneldata */
#define	TCS34725_CDATAH				(0x15)
#define	TCS34725_RDATAL				(0x16)	/* Redchanneldata */
#define	TCS34725_RDATAH				(0x17)
#define	TCS34725_GDATAL				(0x18)	/* Greenchanneldata */
#define	TCS34725_GDATAH				(0x19)
#define	TCS34725_BDATAL				(0x1A)	/* Bluechanneldata */
#define	TCS34725_BDATAH				(0x1B)

typedef enum
{
	TCS34725_INTEGRATIONTIME_2_4MS	= 0xFF,	/* <2.4ms-1cycle-MaxCount:1024 */
	TCS34725_INTEGRATIONTIME_24MS	= 0xF6,	/* <24ms-10cycles-MaxCount:10240 */
	TCS34725_INTEGRATIONTIME_50MS	= 0xEB,	/* <50ms-20cycles-MaxCount:20480 */
	TCS34725_INTEGRATIONTIME_101MS	= 0xD5,	/* <101ms-42cycles-MaxCount:43008 */
	TCS34725_INTEGRATIONTIME_154MS	= 0xC0,	/* <154ms-64cycles-MaxCount:65535 */
	TCS34725_INTEGRATIONTIME_700MS	= 0x00	/* <700ms-256cycles-MaxCount:65535 */
} tcs34725IntegrationTime_t;

typedef enum
{
	TCS34725_GAIN_1X	= 0x00,	/* <Nogain */
	TCS34725_GAIN_4X	= 0x01,	/* <2xgain */
	TCS34725_GAIN_16X	= 0x02,	/* <16xgain */
	TCS34725_GAIN_60X	= 0x03	/* <60xgain */
} tcs34725Gain_t;

typedef enum
{
	NONE	= 0,
	RED		= 1,
	GREEN	= 2,
	BLUE	= 3
} colorChannel_t;
