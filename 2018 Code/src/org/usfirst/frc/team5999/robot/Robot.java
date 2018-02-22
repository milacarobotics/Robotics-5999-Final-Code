/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5999.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();

	Joystick LStick = new Joystick(0);
	Joystick RStick = new Joystick(1);
	
	
	
	VictorSP FL = new VictorSP(0);
	VictorSP FR = new VictorSP(2);
	VictorSP BL = new VictorSP(1);
	VictorSP BR = new VictorSP(3); 
	

	
	
	SpeedControllerGroup LDrive = new SpeedControllerGroup(FL, BL);
	SpeedControllerGroup RDrive = new SpeedControllerGroup(FR, BR);

	DifferentialDrive Tank = new DifferentialDrive(LDrive,RDrive);
	
	VictorSP Arm1= new VictorSP(6);
	VictorSP Arm2 = new VictorSP(7);
	
	SpeedControllerGroup Arm = new SpeedControllerGroup(Arm1, Arm2);
	
	
	
	Spark Grab1 = new Spark(4);
	Spark Grab2 = new Spark (5);
	
	
	SpeedControllerGroup Grab = new SpeedControllerGroup (Grab1,Grab2);
	
	// compressor
	Compressor comp = new Compressor();
	
	
	// wrist
	DoubleSolenoid Wrist1 = new DoubleSolenoid(0,1);
	Solenoid Wrist2 = new Solenoid(2);
	
	
	
	Timer BreakingBot = new Timer();
	
	
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		CameraServer.getInstance().startAutomaticCapture();
		
		Wrist1.set(Value.kForward);
		Wrist1.set(Value.kReverse);
		
		Wrist2.set(true);
		Wrist2.set(false);
	
	}
	
	



	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		BreakingBot.reset();
		BreakingBot.start();
		
	}


	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:

				
				while(BreakingBot.get() < 10) {
					
					SmartDashboard.putNumber("Timer", BreakingBot.get());
					boolean start = BreakingBot.get() >= 0;
					boolean stop = BreakingBot.get() <= 4;
					
					if(start == true) {
						Tank.tankDrive(.5, .5);
					
					}else if(stop == true) {
						Tank.stopMotor();
					}else {
						Tank.stopMotor();					
					}
					SmartDashboard.putNumber("Timer", BreakingBot.get());
				}

					/*Tank.stopMotor();
				}
					*/
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}
	
		
	}


	@Override
	public void teleopPeriodic() {
		Tank.tankDrive(-LStick.getY()*.75, -RStick.getY()*.75);
	
		//Grabber//
		
		if(LStick.getRawButton(1) == true) {
			Grab.set(.5);
		}else if (LStick.getRawButton(2)== true) {
			Grab.set(-.3);
		}else {
			Grab.set(.1);
		}
	
		//Wrist//
		
		if(RStick.getRawButton(1)){
			Wrist1.set(Value.kForward);
			Wrist2.set(true);
		} else if(RStick.getRawButton(2)) {
			Wrist1.set(Value.kReverse);
			Wrist2.set(false);
		} else {
			Wrist1.set(Value.kOff);
			Wrist2.set(false);
		}
	
	//Arm//
	
	if(LStick.getRawButton(5)== true) {
		Arm.set(.25);
	}else if (LStick.getRawButton(6)== true) {
		Arm.set(-.25);
	}else {
		Arm.stopMotor();
		
	}
	
	
	
		SmartDashboard.putData("Grabber", Grab);
		
	}


	@Override
	public void testPeriodic() {
	
		
		LiveWindow.add(Grab);
	}
}
