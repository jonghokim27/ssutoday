import React from 'react';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import MainScreen from '../screens/MainScreen';
import LoginNavigator from './LoginNavigator';
import {NavigationContainer} from '@react-navigation/native';
import NoticeNavigator from './NoticeNavigator';
import MyNavigator from './MyNavigator';
import ReserveNavigator from './ReserveNavigator';
import LMSNavigator from './LMSNavigator';

const Tab = createBottomTabNavigator();

const RootNavigator = () => {
  return (
    <Tab.Navigator
      screenOptions={{
        headerShown: false,
        tabBarStyle: {display: 'none'},
        unmountOnBlur: true,
      }}>
      <Tab.Screen name="MainScreen" component={MainScreen} />
      <Tab.Screen name="LoginNavigator" component={LoginNavigator} />
      <Tab.Screen name="NoticeNavigator" component={NoticeNavigator} />
      <Tab.Screen name="ReserveNavigator" component={ReserveNavigator} />
      <Tab.Screen name="LMSNavigator" component={LMSNavigator} />
      <Tab.Screen name="MyNavigator" component={MyNavigator} />
    </Tab.Navigator>
  );
};

export default RootNavigator;
