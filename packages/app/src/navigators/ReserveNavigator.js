import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import ReserveScreen from '../screens/ReserveScreen';
import ReserveRoomScreen from '../screens/ReserveRoomScreen';
import ReserveListScreen from '../screens/ReserveListScreen';

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
    </Stack.Navigator>
  );
};

export default ReserveNavigator;
