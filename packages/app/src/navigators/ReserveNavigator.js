import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import ReserveScreen from '../screens/ReserveScreen';
import ReserveRoomScreen from '../screens/ReserveRoomScreen';
import ReserveListScreen from '../screens/ReserveListScreen';
import ReserveReportScreen from '../screens/ReserveReportScreen';
import ReserveLockerScreen from '../screens/ReserveLockerScreen';
import ReservePhotoShootScreen from '../screens/ReservePhotoShootScreen';
import ReservePhotoViewScreen from '../screens/ReservePhotoViewScreen';

const Stack = createNativeStackNavigator();

const ReserveNavigator = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: false,
      }}>
      <Stack.Screen name="ReserveScreen" component={ReserveScreen} />
      <Stack.Screen name="ReserveRoomScreen" component={ReserveRoomScreen} />
      <Stack.Screen name="ReserveListScreen" component={ReserveListScreen} />
      <Stack.Screen
        name="ReserveReportScreen"
        component={ReserveReportScreen}
      />
      <Stack.Screen
        name="ReserveLockerScreen"
        component={ReserveLockerScreen}
      />
      <Stack.Screen
        name="ReservePhotoShootScreen"
        component={ReservePhotoShootScreen}
      />
      <Stack.Screen
        name="ReservePhotoViewScreen"
        component={ReservePhotoViewScreen}
      />
    </Stack.Navigator>
  );
};

export default ReserveNavigator;
