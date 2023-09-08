import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import NoticeScreen from '../screens/NoticeScreen';
import NoticeWebViewScreen from '../screens/NoticeWebViewScreen';

const Stack = createNativeStackNavigator();

const NoticeNavigator = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: false,
      }}>
      <Stack.Screen name="NoticeScreen" component={NoticeScreen} />
      <Stack.Screen
        name="NoticeWebViewScreen"
        component={NoticeWebViewScreen}
      />
    </Stack.Navigator>
  );
};

export default NoticeNavigator;
