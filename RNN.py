import torch.utils.data as Data
import datetime
import itertools
import torch.optim as optim
import torch
from model_RNN import model_RNN
import torch.nn as nn
import maximum_mean_descripancy

from tool.imblearn.over_sampling import RandomOverSampler
from ParsingSource import *
from Tools import *



# -------Auxiliary method--------start
def init_record_data():
    record_data = {'Training Set': [], 'Test Set': [], 'Model': [], 'Embedding Dim': [], 'Number of Filter': [],
                   'Filter Size': [], 'Number of Hidden Nodes': [], 'Learning Rate': [], 'Momentun': [],
                   'L2 Weight': [], 'Dropout': [], 'Number of Epoch': [], 'Stride': [], 'Padding': [],
                   'Batch Size': [], 'Pool Size': [], 'MMD Lambda': [], 'MMD Gamma': [],
                   'Loop Size': [], 'accuracy_mean': [], 'accuracy_std': [], 'AUC_mean': [],
                   'AUC_std': [], 'F-measure_mean': [], 'F-measure_std': [], 'MCC_mean': [], 'MCC_std': []}
    return record_data


def insert_param(training, test, model_name, nn_params, mmd_params, loop_size, data):
    data['Training Set'].append(training)
    data['Test Set'].append(test)
    data['Model'].append(model_name)

    data['Embedding Dim'].append(nn_params['EMBED_DIM'])
    data['Number of Filter'].append(nn_params['N_FILTER'])
    data['Filter Size'].append(nn_params['FILTER_SIZE'])
    data['Number of Hidden Nodes'].append(nn_params['N_HIDDEN_NODE'])
    data['Learning Rate'].append(nn_params['LEARNING_RATE'])
    data['Momentun'].append(nn_params['MOMEMTUN'])
    data['L2 Weight'].append(nn_params['L2_WEIGHT'])
    data['Dropout'].append(nn_params['DROPOUT'])
    data['Number of Epoch'].append(nn_params['N_EPOCH'])
    data['Stride'].append(nn_params['STRIDE'])
    data['Padding'].append(nn_params['PADDING'])
    data['Batch Size'].append(nn_params['BATCH_SIZE'])
    data['Pool Size'].append(nn_params['POOL_SIZE'])

    data['MMD Lambda'].append(mmd_params['MMD_LAMBDA'])
    data['MMD Gamma'].append(mmd_params['MMD_GAMMA'])
    data['Loop Size'].append(str(loop_size))


def insert_result(acc_m, acc_s, auc_m, auc_s, f1_m, f1_s, mcc_m, mcc_s, data):
    data['accuracy_mean'].append(round(acc_m, 3))
    data['accuracy_std'].append(round(acc_s, 3))
    data['AUC_mean'].append(round(auc_m, 3))
    data['AUC_std'].append(round(auc_s, 3))
    data['F-measure_mean'].append(round(f1_m, 3))
    data['F-measure_std'].append(round(f1_s, 3))
    data['MCC_mean'].append(round(mcc_m, 3))
    data['MCC_std'].append(round(mcc_s, 3))


def save_data(file_name, data):
    df = pd.DataFrame(data=data,
                      columns=['Training Set', 'Test Set', 'Model', 'Embedding Dim', 'Number of Filter', 'Filter Size',
                               'Number of Hidden Nodes', 'Learning Rate', 'Momentun', 'L2 Weight', 'Dropout',
                               'Number of Epoch', 'Stride', 'Padding', 'Batch Size', 'Pool Size',
                               'MMD Lambda', 'MMD Gamma', 'Loop Size', 'accuracy_mean', 'accuracy_std',
                               'AUC_mean', 'AUC_std', 'F-measure_mean', 'F-measure_std', 'MCC_mean', 'MCC_std'])

    save_path = 'result/' + file_name + '.csv'
    if os.path.exists(save_path):
        df.to_csv(save_path, mode='a', header=False, index=False)
    else:
        df.to_csv(save_path, mode='w', index=False)


def calculate_save_data(model_name, path0, path1, nn_params, mmd_params, LOOP_SIZE, acc, auc, f1, mcc):
    # Calculate the mean and standard deviation
    acc, auc, f1, mcc = np.array(acc), np.array(auc), np.array(f1), np.array(mcc)
    acc_m, acc_s, auc_m, auc_s, f1_m, f1_s, mcc_m, mcc_s = acc.mean(), acc.std(), auc.mean(), auc.std(), f1.mean(), f1.std(), mcc.mean(), mcc.std()

    # Save the results in a file
    record_data = init_record_data()
    insert_result(acc_m, acc_s, auc_m, auc_s, f1_m, f1_s, mcc_m, mcc_s, data=record_data)
    insert_param(training=path0, test=path1, model_name=model_name, nn_params=nn_params, mmd_params=mmd_params, loop_size=LOOP_SIZE, data=record_data)
    save_data(result_file_name, record_data)

# -------Auxiliary method--------end

# Initial super-parameter of network
init_cnn_params = {'EMBED_DIM': 30, 'N_FILTER': 10, 'FILTER_SIZE': 5, 'N_HIDDEN_NODE': 100, 'N_EPOCH': 15, 'BATCH_SIZE': 32, 'LEARNING_RATE': 1e-5,
                   'MOMEMTUN': 0.9, 'L2_WEIGHT': 0.005, 'DROPOUT': 0.5, 'STRIDE': 1, 'PADDING': 0, 'POOL_SIZE': 2, 'DICT_SIZE': 0, 'TOKEN_SIZE': 0}

# Initial super-parameter of MMD
init_mmd_params = {'MMD_LAMBDA': 0.25, 'MMD_GAMMA': 5}


# Adjustable parameter
REGENERATE = False
dump_data_path = 'data/balanced_dump_data_1549189395/'
result_file_name = 'RNN Result'
LOOP_SIZE = 1  # 20

opt_lambda = [1]  # [1, 10]
opt_gamma = [5]

opt_node = [100]
opt_batchsize = [32]
opt_epoch = [15]  # 15
opt_learning_rate = [1e-5]

# Fixed parameter
IMBALANCE_PROCESSOR = RandomOverSampler()  # RandomOverSampler(), RandomUnderSampler(), None, 'cost'
HANDCRAFT_DIM = 20
DEVICE = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
root_path_source = 'data/projects/'
root_path_csv = 'data/csvs/'
package_heads = ['org', 'gnu', 'bsh', 'javax', 'com']

# Start Time
start_time = datetime.datetime.now()
start_time_str = start_time.strftime('%Y-%m-%d_%H.%M.%S')

# Get a list of source and target projects
path_train_and_test = []
with open('data/pairs-one.txt', 'r') as file_obj:
    for line in file_obj.readlines():
        line = line.strip('\n')
        line = line.strip(' ')
        path_train_and_test.append(line.split(','))

# Loop each pair of combinations
for path in path_train_and_test:

    # Get file
    path_train_source = root_path_source + path[0]
    path_train_handcraft = root_path_csv + path[0] + '.csv'
    path_test_source = root_path_source + path[1]
    path_test_handcraft = root_path_csv + path[1] + '.csv'

    # Regenerate token or get from dump_data
    print(path[0] + "===" + path[1])
    train_project_name = path_train_source.split('/')[2]
    test_project_name = path_test_source.split('/')[2]
    path_train_and_test_set = dump_data_path + train_project_name + '_to_' + test_project_name
    # If you don't need to regenerate, get it directly from dump_data
    if os.path.exists(path_train_and_test_set) and not REGENERATE:
        obj = load_data(path_train_and_test_set)
        [train_ast, train_hand_craft, train_label, test_ast, test_hand_craft, test_label, vector_len, vocabulary_size] = obj
    else:
        # Get a list of instances of the training and test sets
        train_file_instances = extract_handcraft_instances(path_train_handcraft)
        test_file_instances = extract_handcraft_instances(path_test_handcraft)

        # Get tokens
        dict_token_train = parse_source(path_train_source, train_file_instances, package_heads)
        dict_token_test = parse_source(path_test_source, test_file_instances, package_heads)

        # Turn tokens into numbers
        list_dict, vector_len, vocabulary_size = transform_token_to_number([dict_token_train, dict_token_test])
        dict_encoding_train = list_dict[0]
        dict_encoding_test = list_dict[1]

        # Take out data that can be used for training
        train_ast, train_hand_craft, train_label = extract_data(path_train_handcraft, dict_encoding_train)
        test_ast, test_hand_craft, test_label = extract_data(path_test_handcraft, dict_encoding_test)

        # Imbalanced processing
        train_ast, train_hand_craft, train_label = imbalance_process(train_ast, train_hand_craft, train_label, IMBALANCE_PROCESSOR)

        # Saved to dump_data
        obj = [train_ast, train_hand_craft, train_label, test_ast, test_hand_craft, test_label, vector_len, vocabulary_size]
        dump_data(path_train_and_test_set, obj)

    # ZScore
    train_hand_craft = (train_hand_craft - np.mean(train_hand_craft, axis=0)) / np.std(train_hand_craft, axis=0)
    test_hand_craft = (test_hand_craft - np.mean(test_hand_craft, axis=0)) / np.std(test_hand_craft, axis=0)

    # Data from numpy to tensor
    train_ast = torch.Tensor(train_ast).to(DEVICE)
    test_ast = torch.Tensor(test_ast).to(DEVICE)

    # Select nn parameters
    nn_params = init_cnn_params.copy()
    nn_params['DICT_SIZE'] = vocabulary_size + 1
    nn_params['TOKEN_SIZE'] = vector_len

    # Select MMD parameters
    mmd_params = init_mmd_params.copy()

    # 数据集
    train_dataset = Data.TensorDataset(train_ast, torch.Tensor(train_label).to(DEVICE))
    # nn_params['BATCH_SIZE'] = len(train_ast)  # 用full size batch

    for batch_size in opt_batchsize:
        # Manufacture loader according to different batchsize
        loader = Data.DataLoader(dataset=train_dataset, batch_size=batch_size, shuffle=True)

        for params_i in itertools.product(opt_node, opt_epoch, opt_learning_rate, opt_lambda, opt_gamma):
            # Select nn parameters
            nn_params['N_HIDDEN_NODE', 'LAMBDA'] = params_i[0]
            nn_params['N_EPOCH'] = params_i[1]
            nn_params['LEARNING_RATE'] = params_i[2]

            # Select MMD parameters
            mmd_params['MMD_LAMBDA'] = params_i[3]
            mmd_params['MMD_GAMMA'] = params_i[4]

            # ------------------ CNN training begins ------------------
            RNN_acc, RNN_auc, RNN_f1, RNN_mcc = [], [], [], []
            DPRNN_acc, DPRNN_auc, DPRNN_f1, DPRNN_mcc = [], [], [], []
            for l in range(LOOP_SIZE):
                model = model_RNN(nn_params)
                model.to(DEVICE)

                # Train
                init_lr = nn_params['LEARNING_RATE']
                for epoch in range(nn_params['N_EPOCH']):
                    optimizer = optim.Adam(model.parameters(), lr=init_lr, betas=(0.9, 0.999), eps=1e-8, weight_decay=nn_params['L2_WEIGHT'], amsgrad=False)
                    # lr = init_lr / math.pow((1 + 10 * (epoch - 1) / cnn_params['N_EPOCH']), 0.75)
                    # optimizer = optim.SGD(model.parameters(), lr=lr, momentum=cnn_params['MOMEMTUN'], weight_decay=cnn_params['L2_WEIGHT'])

                    total_loss_train = 0
                    for step, (batch_ast_x, batch_y) in enumerate(loader):
                        print('epoch - step ' + str(epoch) + ' - ' + str(step))
                        model.train()
                        y_score, y_pred, x_batch_mmd, x_src_mmd, x_tar_mmd = model(batch_ast_x, train_ast, test_ast)

                        criterion = nn.BCELoss().to(DEVICE)
                        batch_y = batch_y.float()
                        loss_c = criterion(y_score, batch_y)
                        with torch.no_grad():
                            loss_mmd = maximum_mean_descripancy.mmd_loss(x_src_mmd, x_tar_mmd, mmd_params['MMD_GAMMA'])

                        # Loss
                        loss = loss_c + mmd_params['MMD_LAMBDA'] * loss_mmd
                        print('loss: ' + str(loss))

                        optimizer.zero_grad()
                        loss.backward()
                        optimizer.step()
                        total_loss_train += loss.data

                    res_e = 'Epoch: [{}/{}], training loss: {:.6f}'.format(epoch, nn_params['N_EPOCH'], total_loss_train / len(loader))
                    print(res_e)

                # RNN
                model_name = 'RNN'
                RNN_acc, RNN_auc, RNN_f1, RNN_mcc = RNN_test(model_name, model, train_ast, test_ast, train_label, test_label, train_hand_craft,
                                                                  test_hand_craft, RNN_acc, RNN_auc, RNN_f1, RNN_mcc)

                # DPRNN
                model_name = 'DPRNN'
                DPRNN_acc, DPRNN_auc, DPRNN_f1, DPRNN_mcc = RNN_test(model_name, model, train_ast, test_ast, train_label, test_label, train_hand_craft,
                                                                          test_hand_craft, DPRNN_acc, DPRNN_auc, DPRNN_f1, DPRNN_mcc)

            # The result is calculated and stored in the file
            calculate_save_data('RNN', path[0], path[1], nn_params, mmd_params, LOOP_SIZE, RNN_acc, RNN_auc, RNN_f1, RNN_mcc)
            calculate_save_data('DPRNN', path[0], path[1], nn_params, mmd_params, LOOP_SIZE, DPRNN_acc, DPRNN_auc, DPRNN_f1, DPRNN_mcc)
            # ------------------ Training End ------------------

# End Time
end_time = datetime.datetime.now()
print(end_time - start_time)
