extends MeshInstance3D

var current_pitch := 0.0
var current_roll := 0.0
var current_yaw := 0.0

func set_orientation(pitch: float, roll: float, yaw: float) -> void:
	current_pitch = pitch
	current_roll = roll
	current_yaw = yaw

	rotation.x = deg_to_rad(pitch)
	rotation.y = deg_to_rad(roll)
	rotation.z = deg_to_rad(yaw)
