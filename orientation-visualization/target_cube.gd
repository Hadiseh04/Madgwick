extends MeshInstance3D

var target_pitch := 0.0
var target_roll := 0.0
var target_yaw := 0.0

func set_target_orientation(pitch: float, roll: float, yaw: float) -> void:
	target_pitch = pitch
	target_roll = roll
	target_yaw = yaw

	rotation.x = deg_to_rad(pitch)
	rotation.y = deg_to_rad(roll)
	rotation.z = deg_to_rad(yaw)
