import React from 'react';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import MyScreen from '../screens/MyScreen';

const Stack = createNativeStackNavigator();

const MyNavigator = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: false,
      }}>
      <Stack.Screen name="MyScreen" component={MyScreen} />
    </Stack.Navigator>
  );
};

export default MyNavigator;
