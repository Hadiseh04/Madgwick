extends Node

enum GameState {
	MEMORIZE,
	PLAYING,
	RESULT
}

@onready var target_cube = $"../TargetCube"
@onready var player_cube = $"../PlayerCube"
@onready var score_label = $"../UI/TopBar/MarginContainer/HBoxContainer/Left/ScoreLabel"
@onready var status_label = $"../UI/TopBar/MarginContainer/HBoxContainer/Center/StatusLabel"
@onready var time_label = $"../UI/TopBar/MarginContainer/HBoxContainer/Right/TimeLabel"
@onready var imu_manager = $"../IMUManager"

const MATCH_TOLERANCE := 15.0
const MEMORIZE_STEP_SECONDS := 1.2
const MEMORIZE_RESET_SECONDS := 0.25

var bridge = null
var method_name := "Madgwick MARG"
var round_duration := 60.0
var current_state = GameState.MEMORIZE
var targets := []
var current_target_index := 0
var memorize_index := 0
var memorize_timer := 0.0
var score := 0
var time_left := 60.0
var current_error := 0.0
var match_locked := false
var result_sent := false
var has_started_playing := false

func _ready() -> void:
	targets = [
		Vector3(-10, 15, 30),
		Vector3(10, 0, 0),
		Vector3(20, -15, -45),
		Vector3(-20, -10, 60),
		Vector3(0, 20, -90)
	]
	targets.shuffle()
	if Engine.has_singleton("OrientationBridge"):
		bridge = Engine.get_singleton("OrientationBridge")
		bridge.game_configured.connect(_on_game_configured)
		method_name = bridge.get_method_name()
		round_duration = float(bridge.get_duration_seconds())

	imu_manager.orientation_changed.connect(_on_orientation_changed)
	await get_tree().process_frame
	start_memorize_sequence()

func _on_game_configured(new_method_name: String, new_duration_seconds: int) -> void:
	method_name = new_method_name
	round_duration = float(new_duration_seconds)
	time_left = round_duration
	_update_status()

func start_memorize_sequence() -> void:
	current_state = GameState.MEMORIZE
	current_target_index = 0
	memorize_index = 0
	memorize_timer = 0.0
	score = 0
	time_left = round_duration
	current_error = 0.0
	match_locked = false
	result_sent = false
	has_started_playing = false
	player_cube.set_orientation(0, 0, 0)
	_update_status()
	
	await get_tree().process_frame

func start_game() -> void:
	current_state = GameState.PLAYING
	current_target_index = 0
	time_left = round_duration
	current_error = 0.0
	match_locked = false
	has_started_playing = true
	_show_current_target()
	_update_status()

func _process(delta: float) -> void:
	match current_state:
		GameState.MEMORIZE:
			_process_memorize(delta)
		GameState.PLAYING:
			_process_playing(delta)

func _process_memorize(delta: float) -> void:
	memorize_timer += delta

	if memorize_timer < MEMORIZE_STEP_SECONDS:
		_show_memorize_target()
	elif memorize_timer < MEMORIZE_STEP_SECONDS + MEMORIZE_RESET_SECONDS:
		target_cube.set_target_orientation(0, 0, 0)
	else:
		memorize_timer = 0.0
		memorize_index += 1

		if memorize_index >= targets.size():
			start_game()
		else:
			_show_memorize_target()

	_update_status()

func _process_playing(delta: float) -> void:
	time_left -= delta

	if time_left <= 0.0:
		time_left = 0.0
		current_state = GameState.RESULT
		_update_status()
		_submit_result_once()
		return

	_update_status()

func _on_orientation_changed(pitch: float, roll: float, yaw: float) -> void:
	if current_state != GameState.PLAYING:
		return

	player_cube.set_orientation(pitch, roll, yaw)

	var target = targets[current_target_index % targets.size()]
	current_error = _calculate_error(
		Vector3(pitch, roll, yaw),
		target
	)

	if current_error <= MATCH_TOLERANCE and !match_locked:
		score += 1
		current_target_index += 1
		match_locked = true
		_show_current_target()

	if match_locked and current_error > MATCH_TOLERANCE * 1.5:
		match_locked = false

	_update_status()

func _show_memorize_target() -> void:
	var target = targets[memorize_index % targets.size()]
	var yaw_to_show = 0.0 if method_name == "Accelerometer + EWMA" else target.z
	target_cube.set_target_orientation(target.x, target.y, yaw_to_show)

func _show_current_target() -> void:
	var target = targets[current_target_index % targets.size()]
	var yaw_to_show = 0.0 if method_name == "Accelerometer + EWMA" else target.z
	target_cube.set_target_orientation(target.x, target.y, yaw_to_show)

func _calculate_error(current: Vector3, target: Vector3) -> float:
	var pitch_error = abs(_angle_difference(current.x, target.x))
	var roll_error = abs(_angle_difference(current.y, target.y))
	if method_name == "Accelerometer + EWMA":
		return (pitch_error + roll_error) / 2.0

	var yaw_error = abs(_angle_difference(current.z, target.z))

	return (pitch_error + roll_error + yaw_error) / 3.0

func _angle_difference(current: float, target: float) -> float:
	var difference = fmod(current - target + 180.0, 360.0)

	if difference < 0.0:
		difference += 360.0

	return difference - 180.0

func _submit_result_once() -> void:
	if result_sent:
		return

	if !has_started_playing:
		return

	result_sent = true

	if bridge != null:
		bridge.submit_result(score, current_error)

func _update_status() -> void:
	match current_state:
		GameState.MEMORIZE:
			score_label.text = ""
			status_label.text = "%s | Memorize %d/%d" % [
				method_name,
				min(memorize_index + 1, targets.size()),
				targets.size()
			]
			time_label.text = ""

		GameState.PLAYING:
			score_label.text = "Score: %d" % score

			status_label.text = "Target: %d/%d | Error: %.1f" % [
				current_target_index % targets.size() + 1,
				targets.size(),
				current_error
			]

			time_label.text = "Time: %.0f" % time_left

		GameState.RESULT:
			score_label.text = "Score: %d" % score
			status_label.text = "Result: %d matches" % score
			time_label.text = ""
