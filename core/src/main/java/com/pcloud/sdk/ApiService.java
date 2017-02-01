/*
 * Copyright (c) 2017 pCloud AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pcloud.sdk;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okio.BufferedSource;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * The general interface that exposes pCloud API's methods.
 * <p>
 * The class is a factory for {@linkplain Call} objects which can be used to execute certain operations on a pCloud account.
 * <h3>
 * The ApiService is designed with reuse in mind, it's best used when created once and shared among code using it,
 * mainly due to used connection and thread pooling.</h3>
 * <li>
 * Users should configure and create instances through the {@link Builder} interface.
 * <li>
 * Builder instances can be created via the {@link PCloudSdk#newApiServiceBuilder()} method
 * or from an existing {@linkplain ApiService} instance through the {@linkplain #newBuilder()} method.
 * <li>
 * Shared instance's configuration can be changed by using the {@linkplain #newBuilder()} method that will return
 * a pre-configured builder.
 * <li>
 * There is no explicit need to call {@linkplain #shutdown()}, any idle threads and connections will automatically get closed once not used anymore.
 * <li>
 * All methods return non-null {@link Call} instances.
 * <li>
 * All methods are thread-safe.
 * <li>
 * All calls resulting in a collection of objects, return unmodifiable {@link java.util.Collection}-derived objects.</li>
 * <h3>
 * Bear in mind that shutting down a {@link ApiService} instance will also affect all instances sharing the underlying resources.
 * </h3>
 */
@SuppressWarnings("unused")
public interface ApiService {

    /**
     * Load a specified folder.
     * <p>
     * Same as calling {@link #listFolder(long, boolean)} )} with {@code recursive} set to false.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/folder/listfolder.html">documentation page</a>
     *
     * @param folderId {@link RemoteFolder} id
     * @return {@link Call}
     */
    Call<RemoteFolder> listFolder(long folderId);

    /**
     * Load a specified folder.
     * <p>
     * Loads the metadata about the folder with the provided folder id.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/folder/listfolder.html">documentation page</a>.
     *
     * @param folderId    target folder id
     * @param recursively if true, a full folder tree will be returned, otherwise the resulting {@linkplain RemoteFolder folder} will contain only its direct children
     * @return {@link Call}
     */
    Call<RemoteFolder> listFolder(long folderId, boolean recursively);

    /**
     * List a specified folder's children.
     * <p>
     * Loads the metadata about folder's direct children, if any.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/folder/listfolder.html">documentation page</a>.
     *
     * @param folder The {@link RemoteFolder} to list. Must not be null
     * @return {@link Call} resulting in a non-null list of children. An empty list is returned if folder is empty
     * @throws IllegalArgumentException on a null {@code folder} argument
     */
    Call<List<RemoteEntry>> listFiles(RemoteFolder folder);

    /**
     * Create a new folder.
     * <p>Create a new folder in the specified folder</p>
     * <p>For more information, see the related <a href="https://docs.pcloud.com/methods/folder/createfolder.html">documentation page</a>.</p>
     *
     * @param parentFolderId The id of the parent folder for the newly created folder
     * @param folderName     The new folder name
     * @return {@link Call} resulting in the metadata for the new folder
     * @throws IllegalArgumentException on a null {@code folderName} argument.
     */
    Call<RemoteFolder> createFolder(long parentFolderId, String folderName);

    /**
     * Create a folder.
     * <p>
     * Same as calling {@link #createFolder(long, String)} with {@code parentFolderId} taken from {@linkplain RemoteFolder#getFolderId()}.
     *
     * @param parentFolder The parent {@link RemoteFolder} for the newly created folder. Must not be null.
     * @param folderName   The new folder name
     * @return {@link Call}
     * @throws IllegalArgumentException on a null {@code parentFolder} argument.
     * @throws IllegalArgumentException on a null {@code folderName} argument.
     */
    Call<RemoteFolder> createFolder(RemoteFolder parentFolder, String folderName);

    /**
     * Delete a specified folder recursively.
     * <p>
     * Same as calling {@link #deleteFolder(long, boolean)} (long, String)} with {@code recursively} set to false.
     *
     * @param folderId The id if the folder you would like to delete
     * @return {@link Call}
     */
    Call<Boolean> deleteFolder(long folderId);

    /**
     * Delete a specified folder recursively.
     * <p>For more information, see the related documentation pages
     * <a href="https://docs.pcloud.com/methods/folder/deletefolder.html">here</a></p> and <a href="https://docs.pcloud.com/methods/folder/deletefolderrecursive.html">here</a>.
     *
     * @param folderId    The id if the folder you would like to delete
     * @param recursively If set to true, all child files will also be deleted.                    If set to false, the operation will fail on any non-empty folder
     * @return {@link Call} resulting in true if the operation is successful, or false otherwise
     */
    Call<Boolean> deleteFolder(long folderId, boolean recursively);

    /**
     * Delete a specified folder recursively.
     *
     * @param folder The {@link RemoteFolder} you would like to delete. Must not be null.
     * @return the call
     * @throws IllegalArgumentException on a null {@code folder} argument.
     * @see #deleteFolder(long) #deleteFolder(long)
     */
    Call<Boolean> deleteFolder(RemoteFolder folder);

    /**
     * Delete a specified folder recursively.
     *
     * @param folder      The {@link RemoteFolder} you would like to delete. Must not be null.
     * @param recursively the recursively
     * @return the call
     * @throws IllegalArgumentException on a null {@code folder} argument.
     * @see #deleteFolder(long, boolean) #deleteFolder(long, boolean)
     */
    Call<Boolean> deleteFolder(RemoteFolder folder, boolean recursively);

    /**
     * Rename a specified folder.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/folder/renamefolder.html">documentation page</a>.
     * <p>
     * Refer to the file names <a href="https://docs.pcloud.com/structures/filenames.html">documentation page</a> for any rules & restrictions.
     *
     * @param folderId      The id of the folder you would like to rename
     * @param newFolderName The new folder name
     * @return {@link Call} resulting in the renamed folder's metadata.
     * @throws IllegalArgumentException on a null {@code newFolderName} argument.
     */
    Call<RemoteFolder> renameFolder(long folderId, String newFolderName);

    /**
     * Rename a specified folder.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/folder/renamefolder.html">documentation page</a>.
     * <p>
     * Refer to the file names <a href="https://docs.pcloud.com/structures/filenames.html">documentation page</a> for any rules & restrictions.
     *
     * @param folder        The {@link RemoteFolder} you would like to rename. Must not be null.
     * @param newFolderName The new folder name. Must not be null.
     * @return {@link Call} resulting in the renamed folder's metadata.
     * @throws IllegalArgumentException on a null {@code folder} argument.
     * @throws IllegalArgumentException on a null {@code newFolderName} argument.
     * @see #renameFolder(long, String) #renameFolder(long, String)
     */
    Call<RemoteFolder> renameFolder(RemoteFolder folder, String newFolderName);

    /**
     * Change a specified folder's parent.
     * <p>For more information, see the related <a href="https://docs.pcloud.com/methods/folder/renamefolder.html">documentation page</a>.</p>
     *
     * @param folderId   The id of the folder you would like to move
     * @param toFolderId The id of the new parent folder
     * @return {@link Call} resulting in the moved folder's metadata.
     */
    Call<RemoteFolder> moveFolder(long folderId, long toFolderId);

    /**
     * Change a specified folder's parent.
     *
     * @param folder   The {@link RemoteFolder} you would like to move. Must not be null.
     * @param toFolder The new parent {@link RemoteFolder}. Must not be null.
     * @return {@link Call} resulting in the moved folder's metadata.
     * @throws IllegalArgumentException on a null {@code folder} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #moveFolder(long, long) #moveFolder(long, long)
     */
    Call<RemoteFolder> moveFolder(RemoteFolder folder, RemoteFolder toFolder);

    /**
     * Copy specified folder.
     * <p>For more information, see the related <a href="https://docs.pcloud.com/methods/folder/renamefolder.html">documentation page</a>.</p>
     *
     * @param folderId   The id of the folder you would like to copy
     * @param toFolderId The id of the destination folder
     * @return {@link Call} resulting in the copied folder's metadata.
     */
    Call<RemoteFolder> copyFolder(long folderId, long toFolderId);

    /**
     * Copy specified folder.
     *
     * @param folder   The {@link RemoteFolder} you would like to copy. Must not be null.
     * @param toFolder The destination {@link RemoteFolder}. Must not be null.
     * @return {@link Call} resulting in the copied folder's metadata.
     * @throws IllegalArgumentException on a null {@code folder} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #copyFolder(long, long) #copyFolder(long, long)
     */
    Call<RemoteFolder> copyFolder(RemoteFolder folder, RemoteFolder toFolder);

    /**
     * Copy specified folder.
     * <p>For more information, see the related <a href="https://docs.pcloud.com/methods/folder/renamefolder.html">documentation page</a>.</p>
     *
     * @param folderId   The id of the folder you would like to copy
     * @param toFolderId The id of the destination folder
     * @param overwrite  If set to {@code true}, a file (or folder) with the same name in the destination folder will be overwritten.
     * @return {@link Call} resulting in the copied folder's metadata.
     */
    Call<RemoteFolder> copyFolder(long folderId, long toFolderId, boolean overwrite);

    /**
     * Copy specified folder.
     *
     * @param folder    The {@link RemoteFolder} you would like to copy. Must not be null.
     * @param toFolder  The destination {@link RemoteFolder}. Must not be null.
     * @param overwrite If set to {@code true}, a file (or folder) with the same name in the destination folder will be overwritten.
     * @return {@link Call} resulting in the copied folder's metadata.
     * @throws IllegalArgumentException on a null {@code folder} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #copyFolder(long, long) #copyFolder(long, long)
     */
    Call<RemoteFolder> copyFolder(RemoteFolder folder, RemoteFolder toFolder, boolean overwrite);

    /**
     * Create a new file.
     * <p>
     * Same as calling {@link #createFile(RemoteFolder, String, DataSource, Date, ProgressListener)} with null {@code modifiedDate} and {@code listener} arguments.
     *
     * @param folder   The {@link RemoteFolder} where you would like to create the file. Must not be null.
     * @param filename The file name. Must not be null.
     * @param data     {@link DataSource} object providing the file content. Must not be null.
     * @return {@link Call} resulting in the new file's metadata
     * @throws IllegalArgumentException on a null {@code folder} argument.
     * @throws IllegalArgumentException on a null {@code filename} argument.
     * @throws IllegalArgumentException on a null {@code data} argument.
     * @see #createFile(long, String, DataSource, Date, ProgressListener) #createFile(long, String, DataSource, Date, ProgressListener)
     */
    Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data);

    /**
     * Create a new file.
     * <p>
     * Same as calling {@link #createFile(long, String, DataSource, Date, ProgressListener)} with {@code folderId} taken from {@linkplain RemoteFolder#getFolderId()}.
     *
     * @param folder       The {@link RemoteFolder} where you would like to create the file. Must not be null.
     * @param filename     The file name. Must not be null.
     * @param data         {@link DataSource} object providing the file content. Must not be null.
     * @param modifiedDate The last modification date to be used. If set to null, the upload date will be used instead.
     * @param listener     The listener to be used to notify about upload progress. If null, no progress will be reported.
     * @return {@link Call} resulting in the new file's metadata
     * @throws IllegalArgumentException on a null {@code folder} argument.
     * @throws IllegalArgumentException on a null {@code filename} argument.
     * @throws IllegalArgumentException on a null {@code data} argument.
     * @see #createFile(long, String, DataSource, Date, ProgressListener) #createFile(long, String, DataSource, Date, ProgressListener)
     * @see DataSource
     * @see ProgressListener
     */
    Call<RemoteFile> createFile(RemoteFolder folder, String filename, DataSource data, Date modifiedDate, ProgressListener listener);

    /**
     * Create a new file.
     * <p>
     * Same as calling {@link #createFile(long, String, DataSource, Date, ProgressListener)} with null {@code modifiedDate} and {@code listener} arguments.
     *
     * @param folderId The {@link RemoteFolder} id where you would like to create the file
     * @param filename The file name. Must not be null.
     * @param data     {@link DataSource} object providing the file content. Must not be null.
     * @return {@link Call} resulting in the new file's metadata
     * @throws IllegalArgumentException on a null {@code filename} argument.
     * @throws IllegalArgumentException on a null {@code data} argument.
     * @see #createFile(long, String, DataSource, Date, ProgressListener) #createFile(long, String, DataSource, Date, ProgressListener)
     */
    Call<RemoteFile> createFile(long folderId, String filename, DataSource data);

    /**
     * Create a new file.
     * <p>
     * Creates a new file with the specified name in the specified folder.
     * <p>
     * If set, the {@code modifiedDate} parameter will be se tas the last modification date of the file.
     * <p>
     * The provided {@link DataSource} object will be used to populate the file's contents.
     * <p>
     * If a {@link ProgressListener} is provided, it will be notified on every {@code n} bytes uploaded, as set per {@link Builder#progressCallbackThreshold(int)}
     * <p>
     * To create an empty file, call the method with a {@link DataSource#EMPTY} argument.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/uploadfile.html">documentation page</a>.
     *
     * @param folderId     The id of the folder you would like to create the file.
     * @param filename     The file name. Must not be null.
     * @param data         {@link DataSource} object providing the file content. Must not be null.
     * @param modifiedDate The last modification date to be used. If set to null, the upload date will be used instead.
     * @param listener     The listener to be used to notify about upload progress. If null, no progress will be reported.
     * @return {@link Call} resulting in the new file's metadata
     * @throws IllegalArgumentException on a null {@code filename} argument.
     * @throws IllegalArgumentException on a null {@code data} argument.
     * @see DataSource
     * @see ProgressListener
     */
    Call<RemoteFile> createFile(long folderId, String filename, DataSource data, Date modifiedDate, ProgressListener listener);


    /**
     * Delete a specified file.
     * <p>
     * Deletes the provided {@link RemoteFile} entry.
     *
     * @param file target file. Must not be null.
     * @return {@link Call} resulting in true if operation was successful, false otherwise.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @see #deleteFile(long)
     * @see RemoteFile
     */
    Call<Boolean> deleteFile(RemoteFile file);

    /**
     * Delete a specified file.
     * <p>
     * Deletes the remote file with the specified file id.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/deletefile.html">documentation page</a>.
     *
     * @param fileId of the file to be deleted
     * @return {@link Call} resulting in true if operation was successful, false otherwise.
     */
    Call<Boolean> deleteFile(long fileId);

    /**
     * Create a download link for a file
     * <p>
     * If successful, this call will return a {@link FileLink} object, which can be used to download the contents
     * of the specified {@link RemoteFile} entry. See {@link DownloadOptions} for more details on possible options
     * for generating a link.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html">documentation page</a>.
     *
     * @param file    target file. Must not be null.
     * @param options to be used for the link generation. Must not be null.
     * @return {@link Call} resulting in a {@link FileLink}
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code options} argument.
     * @see DownloadOptions
     * @see FileLink
     */
    Call<FileLink> createFileLink(RemoteFile file, DownloadOptions options);

    /**
     * Create a download link for a file
     * <p>
     * If successful, this call will return a {@link FileLink} object, which can be used to download the contents
     * of the remote file with the specified {@code fileId}. See {@link DownloadOptions} for more details on possible options
     * for generating a link.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html">documentation page</a>.
     *
     * @param fileId  the file
     * @param options the options
     * @return {@link Call} resulting in a {@link FileLink}
     * @throws IllegalArgumentException on a null {@code options} argument.
     * @see DownloadOptions
     * @see FileLink
     */
    Call<FileLink> createFileLink(long fileId, DownloadOptions options);

    /**
     * Download a {@link FileLink} to a specified destination.
     * <p>
     * Same as calling {@link #download(FileLink, DataSink, ProgressListener)} with a null {@code listener}.
     *
     * @param fileLink the file link to be downloaded. Must not be null.
     * @param sink     the sink that will receive the data. Must not be null.
     * @return a void {@link Call} which will return on success, or report an error otherwise.
     * @throws IllegalArgumentException on a null {@code fileLink} argument.
     * @throws IllegalArgumentException on a null {@code sink} argument.
     * @see DataSink
     * @see ProgressListener
     * @see FileLink
     */
    Call<Void> download(FileLink fileLink, DataSink sink);

    /**
     * Download a {@link FileLink} to a specified destination.
     * <p>
     * This call will get attempt to download the contents of the file that
     * the provided {@code fileLink} object was created for, optionally notifying
     * about the progress via a provided {@link ProgressListener}. The content bytes will be
     * delivered though the {@link DataSink} object.
     * <p>
     * See {@link Builder#progressCallbackThreshold(int)} for details on
     * how to control the progress notifications rate.
     * <p>
     * If set via {@link Builder#callbackExecutor(Executor)}, the progress listener's
     * methods will be scheduled on the provided Executor.
     * <p>
     * Refer to the file links <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html"> documentation page</a>
     * for details on any {@link ApiError} errors.
     *
     * @param fileLink the file link to be downloaded. Must not be null.
     * @param sink     the sink that will receive the data. Must not be null.
     * @param listener an optional listener that will get notified on progress. If null, no progress will be reported.
     * @return a void {@link Call} which will return on success, or report an error otherwise.
     * @throws IllegalArgumentException on a null {@code fileLink} argument.
     * @throws IllegalArgumentException on a null {@code sink} argument.
     * @see DataSink
     * @see ProgressListener
     * @see FileLink
     */
    Call<Void> download(FileLink fileLink, DataSink sink, ProgressListener listener);

    /**
     * Get the content of a specified remote file.
     * <p>
     * This call is a shorthand for obtaining a {@link FileLink} object via
     * {@link #createFileLink(RemoteFile, DownloadOptions)} with {@link DownloadOptions#DEFAULT},
     * then using it with the {@link #download(FileLink)} method.
     * <p>
     * Refer to the file links <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html"> documentation page</a>
     * for details on any {@link ApiError} errors.
     * <h3>
     * NOTE: It is the caller's responsibility to close the resulting {@link BufferedSource} object
     * or resource leaks will occur.
     * </h3>
     *
     * @param file target file. Must not be null.
     * @return {@link Call} which results in a bytes source
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @see #createFileLink(long, DownloadOptions)
     * @see #download(FileLink)
     */
    Call<BufferedSource> download(RemoteFile file);

    /**
     * Get the content of a specified file link.
     * <p>
     * Upon success, this call will return a bytes source of the file that
     * the provided {@code fileLink} object was created for.
     * <p>
     * Refer to the file links <a href="https://docs.pcloud.com/methods/streaming/getfilelink.html"> documentation page</a>
     * for details on any {@link ApiError} errors.
     * <h3>
     * NOTE: It is the caller's responsibility to close the resulting {@link BufferedSource} object
     * or resource leaks will occur.
     * </h3>
     *
     * @param fileLink the file link to be downloaded. Must not be null.
     * @return {@link Call} which results in a bytes source
     * @throws IllegalArgumentException on a null {@code fileLink} argument.
     */
    Call<BufferedSource> download(FileLink fileLink);

    /**
     * Copy a specified file.
     * <p>
     * Same as calling {@link #copyFile(long, long, boolean)} with {@code overwrite} set to {@code false}.
     *
     * @param fileId     The file id of the file to be copied.
     * @param toFolderId The folder id of the folder where the file will be copied.
     * @return {@link Call} resulting in the metadata of the copied file
     */
    Call<RemoteFile> copyFile(long fileId, long toFolderId);

    /**
     * Copy a specified file.
     * <p>
     * The call will copy the file specified by the {@code fileId} argument to the folder specified by the {@code toFolderId} argument.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/copyfile.html">documentation page</a>.
     *
     * @param fileId     The file id of the file to be copied.
     * @param toFolderId The folder id of the folder where the file will be copied.
     * @param overwrite  If set to {@code true}, a file with the same name in the destination folder will be overwritten
     * @return {@link Call} resulting in the metadata of the copied file
     */
    Call<RemoteFile> copyFile(long fileId, long toFolderId, boolean overwrite);

    /**
     * Copy a specified file.
     * <p>
     * Same as calling {@link #copyFile(long, long, boolean)} with {@code overwrite} set to {@code false}.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/copyfile.html">documentation page</a>.
     *
     * @param file     The {@link RemoteFile} to be copied. Must not be null.
     * @param toFolder The {@link RemoteFolder} where the file will be copied. Must not be null.
     * @return {@link Call} resulting in the metadata of the copied file
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #copyFile(long, long, boolean)
     */
    Call<RemoteFile> copyFile(RemoteFile file, RemoteFolder toFolder);

    /**
     * Copy a specified file.
     * <p>
     * Same as calling {@link #copyFile(long, long, boolean)}
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/copyfile.html">documentation page</a>.
     *
     * @param file      The {@link RemoteFile} to be copied. Must not be null.
     * @param toFolder  The {@link RemoteFolder} where the file will be copied. Must not be null.
     * @param overwrite If set to {@code true}, a file with the same name in the destination folder will be overwritten
     * @return {@link Call} resulting in the metadata of the copied file
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #copyFile(long, long, boolean)
     */
    Call<RemoteFile> copyFile(RemoteFile file, RemoteFolder toFolder, boolean overwrite);

    /**
     * Copy a specified file or folder.
     * <p>
     * The call will copy the file or folder specified by the {@code file} argument to specified {@code toFolder}.
     *
     * @param file     The {@link RemoteEntry} to be copied. Must not be null.
     * @param toFolder The {@link RemoteFolder} where the file will be copied. Must not be null.
     * @return {@link Call} resulting in the metadata of the copied file or folder.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #copyFile(RemoteFile, RemoteFolder, boolean)
     * @see #copyFolder(RemoteFolder, RemoteFolder)
     */
    Call<? extends RemoteEntry> copy(RemoteEntry file, RemoteFolder toFolder);

    /**
     * Copy a specified file or folder.
     * <p>
     * The call will copy the file or folder specified by the {@code file} argument to specified {@code toFolder}.
     * <p>
     * The behavior of the {@code overwriteFiles} parameter depends on the type of the {@code file} being passed.
     * For files, see the description for {@link #copyFile(RemoteFile, RemoteFolder)},
     * otherwise see {@link #copyFolder(RemoteFolder, RemoteFolder, boolean)}
     *
     * @param file           The {@link RemoteEntry} to be copied. Must not be null.
     * @param toFolder       The {@link RemoteFolder} where the file will be copied. Must not be null.
     * @param overwriteFiles If set to {@code true}, a file (or folder) with the same name in the destination folder will be overwritten.
     * @return {@link Call} resulting in the metadata of the copied file or folder.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #copyFile(RemoteFile, RemoteFolder, boolean)
     * @see #copyFolder(RemoteFolder, RemoteFolder)
     */
    Call<? extends RemoteEntry> copy(RemoteEntry file, RemoteFolder toFolder, boolean overwriteFiles);

    /**
     * Copy a specified file or folder.
     * <p>
     * The call will copy the file or folder specified by the {@code id} argument to the specified folder with{@code toFolderId}.
     * <p>
     * The behavior of the {@code overwriteFiles} parameter depends on the type of the file specified by {@code id}.
     * For files, see the description for {@link #copyFile(RemoteFile, RemoteFolder)},
     * otherwise see {@link #copyFolder(RemoteFolder, RemoteFolder, boolean)}
     *
     * @param id         The id of the file or folder to be copied. Must not be null.
     * @param toFolderId The {@link RemoteFolder} where the file will be copied. Must not be null.
     * @return {@link Call} resulting in the metadata of the copied file or folder.
     * @throws IllegalArgumentException on a null {@code id} argument.
     * @see #copyFile(long, long)
     * @see #copyFolder(long, long)
     */
    Call<? extends RemoteEntry> copy(String id, long toFolderId);

    /**
     * Copy a specified file or folder.
     * <p>
     * The call will copy the file or folder specified by the {@code id} argument to the specified folder with{@code toFolderId}.
     * <p>
     * The behavior of the {@code overwriteFiles} parameter depends on the type of the file specified by {@code id}.
     * For files, see the description for {@link #copyFile(RemoteFile, RemoteFolder)},
     * otherwise see {@link #copyFolder(RemoteFolder, RemoteFolder, boolean)}
     *
     * @param id             The id of the file or folder to be copied. Must not be null.
     * @param toFolderId     The {@link RemoteFolder} where the file will be copied. Must not be null.
     * @param overwriteFiles If set to {@code true}, a file (or folder) with the same name in the destination folder will be overwritten.
     * @return {@link Call} resulting in the metadata of the copied file or folder.
     * @throws IllegalArgumentException on a null {@code id} argument.
     * @see #copyFile(long, long, boolean)
     * @see #copyFolder(long, long, boolean)
     */
    Call<? extends RemoteEntry> copy(String id, long toFolderId, boolean overwriteFiles);

    /**
     * Move a specified file or folder.
     * <p>
     * The call will move the file or folder specified by the {@code file} argument to specified {@code toFolder}.
     *
     * @param file     The {@link RemoteEntry} to be moved. Must not be null.
     * @param toFolder The {@link RemoteFolder} where the file will be moved. Must not be null.
     * @return {@link Call} resulting in the metadata of the moved file or folder.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #moveFile(RemoteFile, RemoteFolder)
     * @see #moveFolder(RemoteFolder, RemoteFolder)
     */
    Call<? extends RemoteEntry> move(RemoteEntry file, RemoteFolder toFolder);

    /**
     * Move a specified file or folder.
     * <p>
     * The call will move the file or folder specified by the {@code file} argument to specified {@code toFolder}.
     *
     * @param id         The id of the file or folder to be moved. Must not be null.
     * @param toFolderId The {@link RemoteFolder} where the file will be moved. Must not be null.
     * @return {@link Call} resulting in the metadata of the moved file or folder.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #moveFile(long, long)
     * @see #moveFolder(long, long)
     */
    Call<? extends RemoteEntry> move(String id, long toFolderId);

    /**
     * Delete a specified file or folder.
     * <p>
     * The call will delete the file or folder specified by the {@code file} argument to specified {@code toFolder}.
     *
     * @param file The {@link RemoteEntry} to be deleted. Must not be null.
     * @return {@link Call} resulting in the metadata of the deleted file or folder.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @see #deleteFile(RemoteFile)
     * @see #deleteFolder(RemoteFolder)
     */
    Call<Boolean> delete(RemoteEntry file);

    /**
     * Delete a specified file or folder.
     * <p>
     * The call will delete the file or folder specified by the {@code file} argument to specified {@code toFolder}.
     *
     * @param id The id of the file or folder to be deleted. Must not be null.
     * @return {@link Call} resulting in the metadata of the deleted file or folder.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @see #deleteFile(long)
     * @see #deleteFolder(long)
     */
    Call<Boolean> delete(String id);

    /**
     * Rename a specified file or folder.
     * <p>
     * The call will rename the file or folder specified by the {@code file} argument to specified {@code newFilename}.
     *
     * @param file        The {@link RemoteEntry} to be renamed. Must not be null.
     * @param newFilename The new name. Must not be null.
     * @return {@link Call} resulting in the renamed file's metadata.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code newFilename} argument.
     * @see #renameFile(RemoteFile, String)
     * @see #renameFolder(RemoteFolder, String)
     */
    Call<? extends RemoteEntry> rename(RemoteEntry file, String newFilename);

    /**
     * Rename a specified file or folder.
     * <p>
     * The call will rename the file or folder specified by the {@code file} argument to specified {@code newFilename}.
     *
     * @param id          The id of the file or folder to be renamed. Must not be null.
     * @param newFilename The new name. Must not be null.
     * @return {@link Call} resulting in the renamed file's metadata.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code newFilename} argument.
     * @see #renameFile(long, String)
     * @see #renameFolder(long, String)
     */
    Call<? extends RemoteEntry> rename(String id, String newFilename);

    /**
     * Move a specified file.
     * <p>
     * The call will move the file specified by the {@code fileId} argument to the folder specified by the {@code toFolderId}.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/renamefile.html">documentation page</a>.
     *
     * @param fileId     The file id of the file to be moved.
     * @param toFolderId The folder id of the folder where the file will be moved.
     * @return {@link Call} resulting in the metadata of the moved file
     */
    Call<RemoteFile> moveFile(long fileId, long toFolderId);

    /**
     * Move a specified file.
     * <p>
     * Same as calling {@link #moveFile(long, long)} (long, long)}
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/renamefile.html">documentation page</a>.
     *
     * @param file     The {@link RemoteFile} to be moved. Must not be null.
     * @param toFolder The {@link RemoteFolder} where the file will be moved. Must not be null.
     * @return {@link Call} resulting in the metadata of the moved file
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code toFolder} argument.
     * @see #moveFile(long, long) (long, long)
     */
    Call<RemoteFile> moveFile(RemoteFile file, RemoteFolder toFolder);

    /**
     * Rename a specified file.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/renamefile.html">documentation page</a>.
     * <p>
     * Refer to the file names <a href="https://docs.pcloud.com/structures/filenames.html">documentation page</a> for any rules & restrictions.
     *
     * @param fileId      The id of the folder you would like to rename
     * @param newFilename The new folder name. Must not be null.
     * @return {@link Call} resulting in the renamed file's metadata.
     * @throws IllegalArgumentException on a null {@code newFilename} argument.
     */
    Call<RemoteFile> renameFile(long fileId, String newFilename);

    /**
     * Rename a specified file.
     * <p>
     * Same as calling {@link #renameFile(long, String)}
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/file/renamefile.html">documentation page</a>.
     * <p>
     * Refer to the file names <a href="https://docs.pcloud.com/structures/filenames.html">documentation page</a> for any rules & restrictions.
     *
     * @param file        The {@link RemoteFile} to be renamed. Must not be null.
     * @param newFilename The new folder name. Must not be null.
     * @return {@link Call} resulting in the renamed file's metadata.
     * @throws IllegalArgumentException on a null {@code file} argument.
     * @throws IllegalArgumentException on a null {@code newFilename} argument.
     * @see #renameFile(long, String)
     */
    Call<RemoteFile> renameFile(RemoteFile file, String newFilename);

    /**
     * Get {@link UserInfo} for the current account.
     * <p>
     * The call will return the user details of the current account that has granted access to the current SDK application.
     * <p>
     * See the OAuth <a href="https://docs.pcloud.com/methods/oauth_2.0/authorize.html">documentation page</a> or
     * the notes {@linkplain ApiService here} on how permissions to access accounts are granted.
     * <p>
     * For more information, see the related <a href="https://docs.pcloud.com/methods/general/userinfo.html">documentation page</a>.
     *
     * @return {@link Call} resulting in the accounts details
     */
    Call<UserInfo> getUserInfo();

    /**
     * Create a new shared {@link ApiService} instance.
     *
     * @return a {@link Builder} sharing the same configuration as this instance.
     */
    Builder newBuilder();

    /**
     * Stop this instance and cleanup resources.
     * <p>
     * <li>
     * All calls created by this instance will be cancelled..
     * <li>
     * The provided {@link okhttp3.ConnectionPool} will have all connections evicted.
     * <li>
     * The {@link okhttp3.Dispatcher} will have it's Executor shutdown.
     * <li>
     * If provided, the {@link okhttp3.Cache} instance will closed.
     */
    void shutdown();

    /**
     * A builder for configuring an creating new {@link ApiService} instances.
     *
     * @see ApiService
     * @see ApiService#newBuilder()
     * @see PCloudSdk#newApiServiceBuilder()
     */
    @SuppressWarnings("unused")
    interface Builder {
        /**
         * Sets the response cache to be used to read and write cached responses.
         *
         * @see Cache
         */
        Builder cache(Cache cache);

        /**
         * Sets the connectionPool used to recycle HTTP and HTTPS connections.
         * <p>
         * If unset, a new connection pool with a default configuration will be used.
         *
         * @see ConnectionPool
         */
        Builder connectionPool(ConnectionPool connectionPool);

        /**
         * Sets the {@link Dispatcher} used to set policy and execute asynchronous requests.
         * <p>
         * Must not be null.
         * <p>
         * If unset, a new dispatcher with a default configuration will be used.
         *
         * @see Dispatcher
         */

        Builder dispatcher(Dispatcher dispatcher);

        /**
         * Use an existing {@link OkHttpClient} instance and share the {@link Dispatcher},
         * {@link ConnectionPool} and {@link Cache}
         * <p>
         * Must not be null.
         * <p>
         * Previous calls to {@link #dispatcher(Dispatcher)}, {@link #connectionPool(ConnectionPool)},
         * {@link Cache} will be overriden by this call.
         *
         * @see OkHttpClient
         */
        Builder withClient(OkHttpClient client);

        /**
         * Sets the default read timeout for new connections.
         * <p>
         * A value of 0 means no timeout, otherwise values must be between 1 and
         * {@link Integer#MAX_VALUE} when converted to milliseconds.
         */
        Builder readTimeout(long timeout, TimeUnit timeUnit);

        /**
         * Sets the default write timeout for new connections.
         * <p>
         * A value of 0 means no timeout, otherwise values must be between 1 and
         * {@link Integer#MAX_VALUE} when converted to milliseconds.
         */
        Builder writeTimeout(long timeout, TimeUnit timeUnit);

        /**
         * Sets the default connect timeout for new connections.
         * <p>
         * A value of 0 means no timeout, otherwise values must be between 1 and
         * {@link Integer#MAX_VALUE} when converted to milliseconds.
         */
        Builder connectTimeout(long timeout, TimeUnit timeUnit);

        /**
         * Sets an {@link Authenticator}.
         * <p>
         * Must not be null.
         *
         * @see Authenticator
         */
        Builder authenticator(Authenticator authenticator);

        /**
         * Set an executor to be used when invoking {@link Callback} instances.
         *
         * @see Executor
         */
        Builder callbackExecutor(Executor callbackExecutor);

        /**
         * Define a progress notifications threshold
         * <p>
         * If set, a supplied {@link ProgressListener} instance will be invoked
         * on no less than {@code bytes} of transferred data.
         * <p>
         * Can be used to control thread congestion caused by the invocations
         * of the progress listener.
         *
         * @param bytes the minimal amounts of bytes to be transferred before
         *              notifying about a transfer progress
         * @see ProgressListener
         * @see ApiService#createFile(long, String, DataSource, Date, ProgressListener)
         * @see ApiService#download(FileLink, DataSink, ProgressListener)
         * @see RemoteData#download(DataSink, ProgressListener)
         */
        Builder progressCallbackThreshold(int bytes);

        /**
         * Create a new {@link ApiService} from the provided configuration.
         *
         * @return a non-null {@link ApiService} object
         * @see ApiService
         */
        ApiService create();
    }
}
