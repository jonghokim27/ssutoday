import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import LMSScreen from '../screens/LMSScreen';

const Stack = createNativeStackNavigator();

const LMSNavigator = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: false,
      }}>
      <Stack.Screen name="LMSScreen" component={LMSScreen} />
    </Stack.Navigator>
  );
};

export default LMSNavigator;
